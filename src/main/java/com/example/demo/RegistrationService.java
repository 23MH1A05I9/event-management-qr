package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private EmailService emailService;

    public List<Registration> saveMembers(List<MemberDTO> members, Long eventId) {
        // Check remaining seats first
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        int remaining = event.getRemainingSeats();
        if (members.size() > remaining) {
            throw new RuntimeException("Not enough seats! Only " + remaining + " seats remaining.");
        }

        List<Registration> saved = new ArrayList<>();

        for (MemberDTO dto : members) {
            Registration reg = new Registration();
            reg.setName(dto.getName());
            reg.setEmail(dto.getEmail());
            reg.setAge(dto.getAge());
            reg.setEventName(dto.getEventName());

            String token = UUID.randomUUID().toString();
            reg.setUniqueToken(token);

            String qrContent = "EVT:" + token;
            String qrBase64 = qrCodeService.generateQRCodeBase64(qrContent);
            reg.setQrCode(qrBase64);
            reg.setQrSent(false);
            reg.setVerified(false);

            saved.add(registrationRepository.save(reg));
        }

        // Update seat count in event
        eventRepository.incrementRegisteredCount(eventId, members.size());

        return saved;
    }

    public void sendQRCodes(List<Long> registrationIds) {
        for (Long id : registrationIds) {
            Registration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found: " + id));

            emailService.sendQRCodeEmail(
                reg.getEmail(),
                reg.getName(),
                reg.getEventName(),
                reg.getQrCode()
            );

            reg.setQrSent(true);
            registrationRepository.save(reg);
        }
    }

    // ✅ UPDATED: handles duplicate scan detection
    public VerifyResult verifyQRToken(String token) {
        Registration reg = registrationRepository.findByUniqueToken(token)
            .orElse(null);

        if (reg == null) {
            return new VerifyResult(false, false, null, "❌ Invalid QR Code - Not Registered");
        }

        if (reg.isVerified()) {
            // Already scanned before!
            return new VerifyResult(false, true, reg,
                "⚠️ Already Scanned! This person already entered at " + reg.getVerifiedAt());
        }

        // First time scan - mark as verified
        reg.setVerified(true);
        reg.setVerifiedAt(LocalDateTime.now());
        registrationRepository.save(reg);

        return new VerifyResult(true, false, reg, "✅ Person Allowed!");
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    // Inner class to hold scan result
    public static class VerifyResult {
        public boolean allowed;
        public boolean alreadyScanned;
        public Registration registration;
        public String message;

        public VerifyResult(boolean allowed, boolean alreadyScanned,
                            Registration registration, String message) {
            this.allowed = allowed;
            this.alreadyScanned = alreadyScanned;
            this.registration = registration;
            this.message = message;
        }
    }
}