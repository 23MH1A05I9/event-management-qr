package com.example.demo;



public class MemberDTO {
    private String name;
    private String email;
    private int age;
    private String eventName;

    // Constructors
    public MemberDTO() {}

    public MemberDTO(String name, String email, int age, String eventName) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.eventName = eventName;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
}