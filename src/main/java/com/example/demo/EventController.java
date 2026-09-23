package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "index";
    }

    @GetMapping("/details")
    public String details(@RequestParam("eventId") Long eventId,
                          @RequestParam("memberCount") int memberCount,
                          Model model) {
        Event event = eventService.getEventById(eventId);

        // Check if enough seats
        if (memberCount > event.getRemainingSeats()) {
            model.addAttribute("events", eventService.getAllEvents());
            model.addAttribute("error", "Only " + event.getRemainingSeats() + " seats remaining for " + event.getName());
            return "index";
        }

        model.addAttribute("event", event);
        model.addAttribute("memberCount", memberCount);
        return "details";
    }

    @PostMapping("/submit")
    public String submitDetails(@RequestParam("memberCount") int memberCount,
                                @RequestParam("eventName") String eventName,
                                @RequestParam("eventId") Long eventId,
                                @RequestParam List<String> names,
                                @RequestParam List<String> emails,
                                @RequestParam List<Integer> ages,
                                Model model) {
        try {
            List<MemberDTO> members = new ArrayList<>();
            for (int i = 0; i < memberCount; i++) {
                MemberDTO dto = new MemberDTO();
                dto.setName(names.get(i));
                dto.setEmail(emails.get(i));
                dto.setAge(ages.get(i));
                dto.setEventName(eventName);
                members.add(dto);
            }

            List<Registration> registrations = registrationService.saveMembers(members, eventId);
            model.addAttribute("registrations", registrations);
            model.addAttribute("eventName", eventName);
            return "confirmation";

        } catch (Exception e) {
            model.addAttribute("events", eventService.getAllEvents());
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }

    @GetMapping("/scanner")
    public String scanner() {
        return "scanner";
    }
 // Admin page to view all data
    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("registrations", registrationService.getAllRegistrations());
        model.addAttribute("events", eventService.getAllEvents());
        return "admin";
    }
}