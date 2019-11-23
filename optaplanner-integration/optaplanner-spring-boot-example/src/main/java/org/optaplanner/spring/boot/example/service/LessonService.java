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

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.optaplanner.spring.boot.example.domain.Lesson;
import org.springframework.stereotype.Service;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public Lesson createLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    public List<Lesson> createLessons(List<Lesson> lessons) {
        return lessonRepository.saveAll(lessons);
    }

    public Optional<Lesson> getLessonById(Long id) {
        return lessonRepository.findById(id);
    }

    public List<Lesson> getLessonList() {
        return lessonRepository.findAll();
    }

    public Lesson updateLesson(Lesson lesson) {
        Lesson lessonEntity = lessonRepository
                .findById(lesson.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Cannot find a Lesson entity with id (%s).", lesson.getId())));
        lessonEntity.setRoom(lesson.getRoom());
        lessonEntity.setTimeslot(lesson.getTimeslot());
        return lessonRepository.save(lessonEntity);
    }
}
