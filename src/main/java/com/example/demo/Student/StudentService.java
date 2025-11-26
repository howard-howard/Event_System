package com.example.demo.Student;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.User;

@Service
public class StudentService {
    
    private final StudentRepo studentRepo;
    private final BCryptPasswordEncoder passwordEncoder; 

    
    @Autowired 
    public StudentService(StudentRepo studentRepo, BCryptPasswordEncoder passwordEncoder){
        this.studentRepo = studentRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Student save(Student student){
        return studentRepo.save(student);
    }

    public List<Student> findAll() {
        return studentRepo.findAll();
    }

    public Student findById(Long id) {
        return studentRepo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        studentRepo.deleteById(id);
    }

    public UserDetails loadStudentByUsername(String email) throws UsernameNotFoundException {
        Student student = studentRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found: " + email));

        return User.builder()
                .username(student.getEmail())
                .password(student.getPassword())
                .roles(student.getRole())       
                .build();
    }

    @Async 
    public CompletableFuture<List<Student>> saveStudentsFromCsv(MultipartFile file) {
        long start = System.currentTimeMillis();

        try {
            
            List<Student> students = StudentCSVHelper.csvToStudents(file);

            for (Student student : students) {
    
                String encodedPassword = passwordEncoder.encode(student.getPassword());
                student.setPassword(encodedPassword);
                
            }

            List<Student> savedStudents = studentRepo.saveAll(students);

            long end = System.currentTimeMillis();
            System.out.println("ASYNC ETL JOB: Processed " + savedStudents.size() + " students in " + (end - start) + "ms");

            return CompletableFuture.completedFuture(savedStudents);

        } catch (Exception e) {
            System.err.println("Failed to process CSV import: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

}
