package com.example.demo.Config;

import com.example.demo.Student.Student;
import com.example.demo.Student.StudentRepo;
import com.example.demo.Admin.Admin;
import com.example.demo.Admin.AdminRepo;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final StudentRepo studentRepository;
    private final AdminRepo adminRepository;

    @Autowired
    public SecurityConfig(StudentRepo studentRepository, AdminRepo adminRepository) {
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            
            Optional<Student> studentOpt = studentRepository.findByEmail(username);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                return org.springframework.security.core.userdetails.User
                        .withUsername(student.getEmail())
                        .password(student.getPassword())
                        .roles(student.getRole())
                        .build();
            }

            
            Optional<Admin> adminOpt = adminRepository.findByEmail(username);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                return org.springframework.security.core.userdetails.User
                        .withUsername(admin.getEmail())
                        .password(admin.getPassword())
                        .roles(admin.getRole())
                        .build();
            }

            throw new UsernameNotFoundException("User not found: " + username);
        };
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/events/all", "/api/events/{id}", "/api/student/all", "/api/student/{id}" ).permitAll()
                .requestMatchers("/", "/home", "/events", "/login").permitAll()
                .requestMatchers("/student/**", "/event/{id}/register").hasAnyRole("Student", "ADMIN")
                .requestMatchers("/event/**",  "/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler())
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/login")
            );
        return http.build();
    }


    
}