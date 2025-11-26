// package com.example.demo.Component;

// import com.example.demo.Lecturer.Lecturer;
// import com.example.demo.Lecturer.LecturerRepo;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Component;

// @Component
// public class DataInitializer implements CommandLineRunner {

//     @Autowired
//     private LecturerRepo lecturerRepository;

//     @Autowired
//     private BCryptPasswordEncoder passwordEncoder;

//     @Override
//     public void run(String... args) throws Exception {
//         if (lecturerRepository.count() == 0) {
//             Lecturer lecturer = new Lecturer(
//                 "Test Lecturer",
//                 "1234567890",
//                 "test@example.com",
//                 passwordEncoder.encode("password123")
//             );
//             lecturerRepository.save(lecturer);
//         }
//     }
// }