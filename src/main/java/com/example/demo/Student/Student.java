package com.example.demo.Student;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.EventStudent.EventStudent;

import jakarta.persistence.*;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "tel_no")
    private String tel_no;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    private String role = "STUDENT";

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<EventStudent> rosters = new ArrayList<>();

    public Student(){}

    public Student(String name, String tel_no, String email, String password){
        this.name = name;
        this.tel_no = tel_no;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTel_no() {
        return tel_no;
    }
    public void setTel_no(String tel_no) {
        this.tel_no = tel_no;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
}
