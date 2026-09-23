package com.example.demo;



import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendQRCodeEmail(String toEmail, String personName, 
                                 String eventName, String qrBase64) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("🎉 Your Event QR Code - " + eventName);

            String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto;'>"
                + "<div style='background: linear-gradient(135deg, #667eea, #764ba2); padding: 30px; text-align: center;'>"
                + "<h1 style='color: white; margin: 0;'>🎟️ Event Pass</h1>"
                + "</div>"
                + "<div style='padding: 30px; background: #f9f9f9;'>"
                + "<h2 style='color: #333;'>Hello, " + personName + "!</h2>"
                + "<p style='color: #666; font-size: 16px;'>You are registered for <strong>" + eventName + "</strong>.</p>"
                + "<p style='color: #666;'>Please present the QR code below at the event entrance.</p>"
                + "<div style='text-align: center; margin: 30px 0;'>"
                + "<img src='cid:qrcode' style='width: 250px; height: 250px; border: 5px solid #667eea; border-radius: 10px;'/>"
                + "</div>"
                + "<p style='color: #999; font-size: 12px; text-align: center;'>This QR code is unique to you. Do not share it.</p>"
                + "</div>"
                + "</div>";

            helper.setText(htmlBody, true);

            // Attach QR code as inline image
            byte[] qrBytes = Base64.getDecoder().decode(qrBase64);
            helper.addInline("qrcode", 
                new org.springframework.core.io.ByteArrayResource(qrBytes), 
                "image/png");

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}