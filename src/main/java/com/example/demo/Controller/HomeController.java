package com.example.demo.Controller;

import com.example.demo.Event.Event;
import com.example.demo.Event.EventRepo;
import com.example.demo.Event.EventService;
import com.example.demo.EventStudent.EventStudentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final EventRepo eventRepository;
    private final EventService eventService;
    private final EventStudentService eventStudentService;
    

    @Autowired
    public HomeController(EventRepo eventRepository, EventService eventService, EventStudentService eventStudentService) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.eventStudentService = eventStudentService;
        logger.info("HomeController initialized with EventRepo");
    }

    @GetMapping("/")
    public String getHome(Model model) {
        logger.info("Fetching all events for home page");
        try {
            List<Event> events = eventRepository.findAll();
            model.addAttribute("events", events);
            logger.info("Events added to model: {}", events.size());
        } catch (Exception e) {
            logger.error("Error fetching events for home page: {}", e.getMessage());
            model.addAttribute("events", Collections.emptyList());
            model.addAttribute("error", "Failed to load events: " + e.getMessage());
        }
        return "home";
    }

    @GetMapping("/home")
    public String goNormal() {
        return "home";
    }

    @PostMapping("/event/{id}/register")
    public String register(@PathVariable Long id, 
                        @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
                        Model model, RedirectAttributes redirectAttributes) {
        try {
            String email = user.getUsername(); 
            String message = eventStudentService.registerStudentToEvent(email, id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }


    @GetMapping("/login")
	String login() {
		return "login";
	}

    @GetMapping("/admin")
    public String adminMenu() {
        return "admin_menu";
    }
    
    
}