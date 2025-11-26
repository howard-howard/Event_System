package com.example.demo.Event;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Event.EventRepo;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class EventAPIController {

    private final EventRepo eventRepo;

    EventAPIController(EventRepo eventRepo){
        this.eventRepo = eventRepo;
    }

    @GetMapping("/api/events/{id}")
    public ResponseEntity<Event> findEventId(@PathVariable Long id) {
        return eventRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/events/all")
    public ResponseEntity<List<Event>> findAllEvent() {
        List<Event> events = eventRepo.findAll();
        return ResponseEntity.ok(events);
    }
    
}

