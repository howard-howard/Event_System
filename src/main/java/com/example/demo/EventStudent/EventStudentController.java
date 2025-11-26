package com.example.demo.EventStudent;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.Event.Event;
import com.example.demo.Event.EventRepo;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;


@Controller
public class EventStudentController {
    
    private final EventStudentService eventStudentService;
    private final EventRepo eventRepo;

    public EventStudentController(EventStudentService eventStudentService, EventRepo eventRepo){
        this.eventStudentService = eventStudentService;
        this.eventRepo = eventRepo;
    }

    @PostMapping("/events/{id}/register")
    public String register(@PathVariable Long id, 
                           @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
                           Model model) {
        String email = user.getUsername(); // Spring Security username = email
        String message = eventStudentService.registerStudentToEvent(email, id);

        model.addAttribute("message", message);
        return "redirect:/events";
    }

    
}
