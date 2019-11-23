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

import org.optaplanner.spring.boot.example.domain.Timeslot;
import org.springframework.stereotype.Service;

@Service
public class TimeslotService {

    private final TimeslotRepository timeslotRepository;

    public TimeslotService(TimeslotRepository timeslotRepository) {
        this.timeslotRepository = timeslotRepository;
    }

    public Timeslot createTimeslot(Timeslot timeslot) {
        return timeslotRepository.save(timeslot);
    }

    public List<Timeslot> createTimeslots(List<Timeslot> timeslots) {
        return timeslotRepository.saveAll(timeslots);
    }

    public Optional<Timeslot> getTimeslotById(Long id) {
        return timeslotRepository.findById(id);
    }

    public List<Timeslot> getTimeslotList() {
        return timeslotRepository.findAll();
    }
}
