package com.example.demo;



import java.util.List;

public class SendQRRequest {
    private List<Long> registrationIds;

    public SendQRRequest() {}

    public List<Long> getRegistrationIds() { return registrationIds; }
    public void setRegistrationIds(List<Long> registrationIds) {
        this.registrationIds = registrationIds;
    }
}