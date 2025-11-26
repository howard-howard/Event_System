package com.example.demo.Student;

import com.example.demo.Student.Student;
import com.example.demo.Student.StudentRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final StudentRepo studentRepository;
    private final StudentService studentService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public StudentController(StudentRepo studentRepository, BCryptPasswordEncoder passwordEncoder, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentService = studentService;
        logger.info("StudentController initialized with StudentRepo and BCryptPasswordEncoder");
    }

    @GetMapping("/students")
    public String listStudents(Model model) {
        logger.info("Fetching all students for students page");
        try {
            model.addAttribute("students", studentRepository.findAll());
            logger.info("Students added to model: {}", studentRepository.findAll().size());
        } catch (Exception e) {
            logger.error("Error fetching students: {}", e.getMessage());
            model.addAttribute("error", "Failed to load students: " + e.getMessage());
        }
        return "students";
    }

    @GetMapping("/student/create")
    public String showCreateForm(Model model) {
        logger.info("Showing student create form");
        model.addAttribute("student", new Student());
        return "student_form";
    }

    @PostMapping("/student/create")
    public String createStudent(
            @RequestParam("name") String name,
            @RequestParam("tel_no") String telNo,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model,
            RedirectAttributes redirectAttributes) {
        logger.info("Creating student: name={}, tel_no={}, email={}", name, telNo, email);
        
        if (name == null || name.trim().isEmpty()) {
            logger.error("Validation failed: Name is required");
            model.addAttribute("error", "Name is required");
            model.addAttribute("student", new Student(name, telNo, email, null));
            return "student_form";
        }
        if (telNo == null || telNo.trim().isEmpty()) {
            logger.error("Validation failed: Telephone number is required");
            model.addAttribute("error", "Telephone number is required");
            model.addAttribute("student", new Student(name, telNo, email, null));
            return "student_form";
        }
        if (email == null || email.trim().isEmpty()) {
            logger.error("Validation failed: Email is required");
            model.addAttribute("error", "Email is required");
            model.addAttribute("student", new Student(name, telNo, email, null));
            return "student_form";
        }
        if (password == null || password.trim().isEmpty()) {
            logger.error("Validation failed: Password is required");
            model.addAttribute("error", "Password is required");
            model.addAttribute("student", new Student(name, telNo, email, null));
            return "student_form";
        }

        try {
            Student student = new Student(name, telNo, email, passwordEncoder.encode(password));
            studentRepository.save(student);
            logger.info("Student created successfully: {}", name);
            redirectAttributes.addFlashAttribute("message", "Student created successfully: " + name);
            return "redirect:/students";
        } catch (Exception e) {
            logger.error("Failed to create student: {}", e.getMessage());
            model.addAttribute("error", "Failed to create student: " + e.getMessage());
            model.addAttribute("student", new Student(name, telNo, email, null));
            return "student_form";
        }
    }

    @GetMapping("/student/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.info("Fetching student with ID: {}", id);
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Invalid student ID: {}", id);
                        return new IllegalArgumentException("Invalid student ID: " + id);
                    });
            model.addAttribute("student", student);
            return "student_form";
        } catch (IllegalArgumentException e) {
            logger.error("Error fetching student: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "student_form";
        }
    }

    @PostMapping("/student/update/{id}")
    public String updateStudent(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("tel_no") String telNo,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model,
            RedirectAttributes redirectAttributes) {
        logger.info("Updating student ID: {}, name={}, tel_no={}, email={}", id, name, telNo, email);
        
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Invalid student ID: {}", id);
                    return new IllegalArgumentException("Invalid student ID: " + id);
                });

        if (name == null || name.trim().isEmpty()) {
            logger.error("Validation failed: Name is required");
            model.addAttribute("error", "Name is required");
            model.addAttribute("student", student);
            return "student_form";
        }
        if (telNo == null || telNo.trim().isEmpty()) {
            logger.error("Validation failed: Telephone number is required");
            model.addAttribute("error", "Telephone number is required");
            model.addAttribute("student", student);
            return "student_form";
        }
        if (email == null || email.trim().isEmpty()) {
            logger.error("Validation failed: Email is required");
            model.addAttribute("error", "Email is required");
            model.addAttribute("student", student);
            return "student_form";
        }
        if (password == null || password.trim().isEmpty()) {
            logger.error("Validation failed: Password is required");
            model.addAttribute("error", "Password is required");
            model.addAttribute("student", student);
            return "student_form";
        }

        try {
            student.setName(name);
            student.setTel_no(telNo);
            student.setEmail(email);
            student.setPassword(passwordEncoder.encode(password));
            studentRepository.save(student);
            logger.info("Student updated successfully: {}", name);
            redirectAttributes.addFlashAttribute("message", "Student updated successfully: " + name);
            return "redirect:/students";
        } catch (Exception e) {
            logger.error("Failed to update student: {}", e.getMessage());
            model.addAttribute("error", "Failed to update student: " + e.getMessage());
            model.addAttribute("student", student);
            return "student_form";
        }
    }

    @PostMapping("/student/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Deleting student ID: {}", id);
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Invalid student ID: {}", id);
                        return new IllegalArgumentException("Invalid student ID: " + id);
                    });
            studentRepository.deleteById(id);
            logger.info("Student deleted successfully: ID={}", id);
            redirectAttributes.addFlashAttribute("message", "Student deleted successfully");
            return "redirect:/students";
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete student: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/students";
        }
    }

    @GetMapping("/student/upload")
    public String showUploadPage() {
        return "upload_student"; 
    }

    @PostMapping("/student/upload")
    public String handleWebUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (StudentCSVHelper.hasCSVFormat(file)) {
            try {
                
                studentService.saveStudentsFromCsv(file);
                redirectAttributes.addFlashAttribute("message", "File uploaded! Processing started in background.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error uploading file: " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Please upload a valid CSV file!");
        }
        return "redirect:/student/upload";
    }

    
}