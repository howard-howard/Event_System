package com.example.demo.Event;
import com.example.demo.Event.Event;
import com.example.demo.EventStudent.EventStudentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class EventController {

    private final EventRepo eventRepository;
    private final EventService eventService;
    private final EventStudentService eventStudentService;

    @Value("${file.upload-dir}")
    private String uploadDir;


    public EventController(EventRepo eventRepository, EventService eventService, EventStudentService eventStudentService) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.eventStudentService = eventStudentService;
    }

    @GetMapping("/events")
    public String showEvents(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
                            Model model) {

        List<Event> events = eventRepository.findAll();
        String email = user.getUsername();

        
        for (Event event : events) {
            boolean registered = eventStudentService.isAlreadyRegistered(email, event.getId());
            event.setAlreadyRegistered(registered);  
        }

        model.addAttribute("events", events);
        return "home"; 
    }

    @GetMapping("/event")
    public String showEvent(Model model) {
        model.addAttribute("all_event", eventRepository.findAll());
        return "event";
    }

    @GetMapping("/event/upload")
    public String showUploadForm(Model model) {
        model.addAttribute("event", new Event());
        return "event_form";
    }

    @PostMapping("/event/upload")
    public String createEvent(
            @RequestParam("organizer") String organizer,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,
            @RequestParam("capacity") Long capacity,
            Model model) {
        
        if (organizer == null || organizer.trim().isEmpty()) {
            model.addAttribute("error", "Organizer is required");
            model.addAttribute("event", new Event(organizer, name, description, null, capacity));
            return "event_form";
        }
        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "Name is required");
            model.addAttribute("event", new Event(organizer, name, description, null, capacity));
            return "event_form";
        }
        if (description == null || description.trim().isEmpty()) {
            model.addAttribute("error", "Description is required");
            model.addAttribute("event", new Event(organizer, name, description, null, capacity));
            return "event_form";
        }
        if (file.isEmpty()) {
            model.addAttribute("error", "Image file is required");
            model.addAttribute("event", new Event(organizer, name, description, null, capacity));
            return "event_form";
        }

        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            System.out.println("Upload directory: " + filePath.toAbsolutePath());

            
            Event event = new Event(organizer, name, description, fileName, capacity);
            eventRepository.save(event);

            model.addAttribute("message", "Event created successfully: " + name);
            model.addAttribute("event", new Event());

        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload file: " + e.getMessage());
            model.addAttribute("event", new Event(organizer, name, description, null, capacity));
        }


        return "event_form";
    }

    @GetMapping("/event/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + id));
        model.addAttribute("event", event);
        return "event_form";
    }

    @PostMapping("/event/update/{id}")
    public String updateEvent(
            @PathVariable Long id,
            @RequestParam("organizer") String organizer,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("capacity") Long capacity,
            Model model) {
        
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + id));

        if (organizer == null || organizer.trim().isEmpty()) {
            model.addAttribute("error", "Organizer is required");
            model.addAttribute("event", event);
            return "event_form";
        }
        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "Name is required");
            model.addAttribute("event", event);
            return "event_form";
        }
        if (description == null || description.trim().isEmpty()) {
            model.addAttribute("error", "Description is required");
            model.addAttribute("event", event);
            return "event_form";
        }

        try {
            event.setOrganizer(organizer);
            event.setName(name);
            event.setDescription(description);
            if (!file.isEmpty()) {
                
                if (event.getFilePath() != null) {
                    Files.deleteIfExists(Paths.get(uploadDir, event.getFilePath()));
                }
                
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());
                event.setFilePath(fileName);
            }
            event.setCapacity(capacity);
            eventRepository.save(event);
            model.addAttribute("message", "Event updated successfully: " + name);
            model.addAttribute("event", event);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload file: " + e.getMessage());
            model.addAttribute("event", event);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update event: " + e.getMessage());
            model.addAttribute("event", event);
        }

        return "event_form";
    }

    @PostMapping("/event/delete/{id}")
    public String deleteEvent(@PathVariable Long id, Model model) {
        try {
            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + id));
            if (event.getFilePath() != null) {
                Files.deleteIfExists(Paths.get(uploadDir, event.getFilePath()));
            }
            eventRepository.deleteById(id);
            model.addAttribute("message", "Event deleted successfully");
        } catch (IOException e) {
            model.addAttribute("error", "Failed to delete file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/events";
    }

    

}