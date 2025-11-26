package com.example.demo.Event;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class EventService {
 
    private final EventRepo eventRepo;

    public EventService(EventRepo eventRepo){
        this.eventRepo = eventRepo;
    }

    public Event save(Event event){
        return eventRepo.save(event);
    }

    public List<Event> findAll() {
        return eventRepo.findAll();
    }

    public Event findById(Long id) {
        return eventRepo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        eventRepo.deleteById(id);
    }

    @Transactional
    public void decreaseCapacity(Long id) {
        Event event = eventRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));

        if (event.getCapacity() == null) {
            throw new IllegalStateException("Capacity not set for this event");
        }

        if (event.getCapacity() <= 0) {
            throw new IllegalStateException("Event is already full");
        }

        event.setCapacity(event.getCapacity() - 1);
        eventRepo.save(event);
    }



}
