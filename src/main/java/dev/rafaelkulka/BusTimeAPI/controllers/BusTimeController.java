package dev.rafaelkulka.BusTimeAPI.controllers;

import dev.rafaelkulka.BusTimeAPI.models.BusTimes.BusTime;
import dev.rafaelkulka.BusTimeAPI.repository.BusTimeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusTimeController {
    @GetMapping("/busTimes")
    public ResponseEntity<BusTime> getAllTimes(){
        return ResponseEntity.status(HttpStatus.OK).body(BusTimeRepository.getBusTimes());
    }
}
