/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.spring.boot.example.service;

import org.optaplanner.spring.boot.example.domain.Lesson;
import org.optaplanner.spring.boot.example.domain.Room;
import org.optaplanner.spring.boot.example.domain.TimeTable;
import org.optaplanner.spring.boot.example.domain.Timeslot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timeTable")
public class TimeTableController {

    private final TimetableService timetableService;

    public TimeTableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    // To try, open http://localhost:8080/timeTable
    @RequestMapping()
    public TimeTable refreshTimeTable() {
        return timetableService.refreshTimeTable();
    }

    @PostMapping("/solve")
    public void solve() {
        timetableService.solve();
    }

    @PostMapping("/addLesson")
    public void addLesson(@RequestBody Lesson lesson) {
        timetableService.addLesson(lesson);
    }

    @PostMapping("/addTimeslot")
    public void addTimeslot(@RequestBody Timeslot timeslot) {
        timetableService.addTimeslot(timeslot);
    }

    // To try:  curl -d '{"name":"Room Z"}' -H "Content-Type: application/json" -X POST http://localhost:8080/timeTable/addRoom
    @PostMapping("/addRoom")
    public void addRoom(@RequestBody Room room) {
        timetableService.addRoom(room);
    }
}
