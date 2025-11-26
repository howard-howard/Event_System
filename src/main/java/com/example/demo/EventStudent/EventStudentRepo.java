package com.example.demo.EventStudent;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Event.Event;
import com.example.demo.Student.Student;

public interface EventStudentRepo extends JpaRepository <EventStudent, Long> {

    boolean existsByStudentAndEvent(Student student, Event event);
    List<EventStudent> findByStudent(Student student);
    
} 
