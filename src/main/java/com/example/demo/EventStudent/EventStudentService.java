package com.example.demo.EventStudent;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.Event.Event;
import com.example.demo.Event.EventRepo;
import com.example.demo.Student.Student;
import com.example.demo.Student.StudentRepo;

import jakarta.transaction.Transactional;

@Service
public class EventStudentService {
    
    private final StudentRepo studentRepo;
    private final EventRepo eventRepo;
    private final EventStudentRepo eventStudentRepo;

    public EventStudentService(StudentRepo studentRepo, EventRepo eventRepo, EventStudentRepo eventStudentRepo) {
        this.studentRepo = studentRepo;
        this.eventRepo = eventRepo;
        this.eventStudentRepo = eventStudentRepo;
    }

    public boolean isAlreadyRegistered(String email, Long eventId) {
        Optional<Student> studentOpt = studentRepo.findByEmail(email);
        if (studentOpt.isEmpty()) {
            return false; 
        }
        Student student = studentOpt.get();
        Optional<Event> eventOpt = eventRepo.findById(eventId);
        if (eventOpt.isEmpty()) {
            return false;
        }
        Event event = eventOpt.get();
        return eventStudentRepo.existsByStudentAndEvent(student, event);
    }

    @Transactional
    public String registerStudentToEvent(String email, Long eventId) {
        Student student = studentRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        
        if (event.getCapacity() <= 0) {
            throw new RuntimeException("Event is full!");
        }

        
        if (eventStudentRepo.existsByStudentAndEvent(student, event)) {
            return "Already registered!";
        }

        EventStudent roster = new EventStudent();
        roster.setStudent(student);
        roster.setEvent(event);
        roster.setAlreadyRegistered(true);

        eventStudentRepo.save(roster);
        event.setCapacity(event.getCapacity() - 1); 
        eventRepo.save(event);

        return "Registration successful!";
    }

    public List<Event> getStudentEvents(String email) {
        Student student = studentRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return eventStudentRepo.findByStudent(student)
                         .stream()
                         .map(EventStudent::getEvent)
                         .toList();
    }
}
