package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/send-qr")
    public ResponseEntity<Map<String, Object>> sendQRCodes(@RequestBody SendQRRequest request) {
        try {
            registrationService.sendQRCodes(request.getRegistrationIds());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "QR codes sent to " + request.getRegistrationIds().size() + " member(s)");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/send-qr/{id}")
    public ResponseEntity<Map<String, Object>> sendSingleQR(@PathVariable Long id) {
        try {
            registrationService.sendQRCodes(List.of(id));
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "QR code sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ✅ UPDATED verify endpoint - handles duplicate scan
    @GetMapping("/verify/{token}")
    public ResponseEntity<Map<String, Object>> verifyQR(@PathVariable String token) {
        RegistrationService.VerifyResult result = registrationService.verifyQRToken(token);

        Map<String, Object> response = new HashMap<>();
        response.put("allowed", result.allowed);
        response.put("alreadyScanned", result.alreadyScanned);
        response.put("message", result.message);

        if (result.registration != null) {
            response.put("name", result.registration.getName());
            response.put("email", result.registration.getEmail());
            response.put("age", result.registration.getAge());
            response.put("event", result.registration.getEventName());
            if (result.registration.getVerifiedAt() != null) {
                response.put("scannedAt", result.registration.getVerifiedAt().toString());
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/registrations")
    public ResponseEntity<List<Registration>> getAllRegistrations() {
        return ResponseEntity.ok(registrationService.getAllRegistrations());
    }
}