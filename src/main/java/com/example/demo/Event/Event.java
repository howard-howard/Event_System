package com.example.demo.Event;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.EventStudent.EventStudent;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "organizer")
    private String organizer;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "capacity")
    private Long capacity;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<EventStudent> rosters = new ArrayList<>();

    public Event() {}

    public Event(String organizer, String name, String description, String filePath, Long capacity) {
        this.organizer = organizer;
        this.name = name;
        this.description = description;
        this.filePath = filePath;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public List<EventStudent> getRosters() {
        return rosters;
    }

    public void setRosters(List<EventStudent> rosters) {
        this.rosters = rosters;
    }

    @Transient
    private boolean alreadyRegistered;

    public boolean isAlreadyRegistered() {
        return alreadyRegistered;
    }

    public void setAlreadyRegistered(boolean alreadyRegistered) {
        this.alreadyRegistered = alreadyRegistered;
    }

}
