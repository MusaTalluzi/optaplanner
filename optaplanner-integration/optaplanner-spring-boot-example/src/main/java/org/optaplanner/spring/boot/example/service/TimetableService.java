/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.spring.boot.example.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.optaplanner.core.api.solver.SolverFuture;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.spring.boot.example.domain.Lesson;
import org.optaplanner.spring.boot.example.domain.Room;
import org.optaplanner.spring.boot.example.domain.TimeTable;
import org.optaplanner.spring.boot.example.domain.Timeslot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class TimetableService {

    @Autowired
    private SolverManager<TimeTable> solverManager;

    private AtomicReference<TimeTable> timeTableReference;
    private SolverFuture solverFuture = null;

    private final RoomService roomService;
    private final TimeslotService timeslotService;
    private final LessonService lessonService;

    public TimetableService(RoomService roomService, TimeslotService timeslotService, LessonService lessonService) {
        this.roomService = roomService;
        this.timeslotService = timeslotService;
        this.lessonService = lessonService;
        timeTableReference = new AtomicReference<>(generateProblem());
    }

    private TimeTable generateProblem() {
        List<Timeslot> timeslotList = new ArrayList<>(10);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
        timeslotList = timeslotService.createTimeslots(timeslotList);

        List<Room> roomList = new ArrayList<>(3);
        roomList.add(new Room("Room A"));
        roomList.add(new Room("Room B"));
        roomList.add(new Room("Room C"));
        roomList = roomService.createRooms(roomList);

        List<Lesson> lessonList = new ArrayList<>();
        lessonList.add(new Lesson("Math", "B. May", "9th grade"));
        lessonList.add(new Lesson("Math", "B. May", "9th grade"));
        lessonList.add(new Lesson("Physics", "M. Curie", "9th grade"));
        lessonList.add(new Lesson("Chemistry", "M. Curie", "9th grade"));
        lessonList.add(new Lesson("Geography", "M. Polo", "9th grade"));
        lessonList.add(new Lesson("History", "I. Jones", "9th grade"));
        lessonList.add(new Lesson("English", "I. Jones", "9th grade"));
        lessonList.add(new Lesson("English", "I. Jones", "9th grade"));
        lessonList.add(new Lesson("Spanish", "P. Cruz", "9th grade"));
        lessonList.add(new Lesson("Spanish", "P. Cruz", "9th grade"));

        lessonList.add(new Lesson("Math", "B. May", "10th grade"));
        lessonList.add(new Lesson("Math", "B. May", "10th grade"));
        lessonList.add(new Lesson("Math", "B. May", "10th grade"));
        lessonList.add(new Lesson("Physics", "M. Curie", "10th grade"));
        lessonList.add(new Lesson("Chemistry", "M. Curie", "10th grade"));
        lessonList.add(new Lesson("Geography", "M. Polo", "10th grade"));
        lessonList.add(new Lesson("History", "I. Jones", "10th grade"));
        lessonList.add(new Lesson("English", "P. Cruz", "10th grade"));
        lessonList.add(new Lesson("Spanish", "P. Cruz", "10th grade"));
        lessonList.add(new Lesson("French", "M. Curie", "10th grade"));

        lessonList.get(4).setTimeslot(timeslotList.get(2));
        lessonList.get(4).setRoom(roomList.get(0));
        lessonList.get(5).setTimeslot(timeslotList.get(3));
        lessonList.get(5).setRoom(roomList.get(1));
        lessonList.get(6).setTimeslot(timeslotList.get(3));
        lessonList.get(6).setRoom(roomList.get(1));
        lessonList = lessonService.createLessons(lessonList);

        return new TimeTable(timeslotList, roomList, lessonList);
    }

    public TimeTable refreshTimeTable() {
        return timeTableReference.get();
    }

    public void solve() {
        // TODO Race condition if room is added while solving, it disappears
        solverFuture = solverManager.solve(timeTableReference.get(), this::updateTimetable);
    }

    private void updateTimetable(TimeTable newTimeTable) {
        newTimeTable.getLessonList().forEach(lessonService::updateLesson);
        timeTableReference.set(newTimeTable);
    }

    public void addLesson(@RequestBody Lesson lesson) {
        timeTableReference.updateAndGet(timeTable -> {
            Lesson newLesson = lessonService.createLesson(lesson);
            timeTable.getLessonList().add(newLesson);
            return timeTable;
        });
        // ProblemFactChange vs. restart solving: Size of problem & frequency of problem fact changes
        solverFuture.terminateEarly();
        solve();
    }

    public void addTimeslot(@RequestBody Timeslot timeslot) {
        timeTableReference.updateAndGet(timeTable -> {
            Timeslot newTimeslot = timeslotService.createTimeslot(timeslot);
            timeTable.getTimeslotList().add(newTimeslot);
            return timeTable;
        });
        solverFuture.terminateEarly();
        solve();
    }

    public void addRoom(@RequestBody Room room) {
        timeTableReference.updateAndGet(timeTable -> {
            Room newRoom = roomService.createRoom(room);
            timeTable.getRoomList().add(newRoom);
            return timeTable;
        });
        solverFuture.terminateEarly();
        solve();
    }
}
