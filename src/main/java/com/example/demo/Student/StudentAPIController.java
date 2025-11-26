package com.example.demo.Student;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Admin.AdminRepo;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class StudentAPIController {

    private final StudentRepo studentRepo;
    private final StudentService studentService;

    StudentAPIController(StudentRepo studentRepo, StudentService studentService){
        this.studentRepo = studentRepo;
        this.studentService = studentService;
    }
    
    @GetMapping("/api/student/{id}")
    public ResponseEntity<Student> findStudentId(@PathVariable Long id) {
        return studentRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("api/student/all")
    public ResponseEntity<List<Student>> findAllStudent(){
        List <Student> students = studentRepo.findAll();
        return ResponseEntity.ok(students);
    }
    
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        if (StudentCSVHelper.hasCSVFormat(file)) {
            
            studentService.saveStudentsFromCsv(file);

            message = "Uploaded the file successfully: " + file.getOriginalFilename() + ". Processing in background.";
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(message); 
        }

        message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
