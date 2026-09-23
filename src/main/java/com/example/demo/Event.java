package com.example.demo;



import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String date;
    private String venue;
    private int maxMembers;
    private int registeredCount; // tracks how many registered

    public Event() {}

    public Event(String name, String description, String date, String venue, int maxMembers) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.venue = venue;
        this.maxMembers = maxMembers;
        this.registeredCount = 0;
    }

    // Calculated: remaining seats
    public int getRemainingSeats() {
        return maxMembers - registeredCount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public int getMaxMembers() { return maxMembers; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }

    public int getRegisteredCount() { return registeredCount; }
    public void setRegisteredCount(int registeredCount) { this.registeredCount = registeredCount; }
}