package com.example.PAMS.controller;

import com.example.PAMS.entities.Doctor;
import com.example.PAMS.entities.Appointment;
import com.example.PAMS.repository.DoctorRepository;
import com.example.PAMS.repository.AppointmentRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorApiController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/{doctorId}/availability")
    public Map<String, Object> getAvailability(
            @PathVariable Integer doctorId,
            @RequestParam String date) {

        LocalDate appointmentDate = LocalDate.parse(date);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DayOfWeek dayOfWeek = appointmentDate.getDayOfWeek();
        String day = dayOfWeek.toString().toLowerCase();
        day = day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase(); // "Wednesday"

        System.out.println("Checking availability for: " + day); // Debug log
        System.out.println("Doctor availability: " + doctor.getAvailability()); // Debug log

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> slots = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            // Remove escape characters if present
            String availabilityJson = doctor.getAvailability().replace("\\\"", "\"");
            System.out.println("Processed JSON: " + availabilityJson); // Debug log

            Map<String, Map<String, String>> availability =
                    mapper.readValue(availabilityJson,
                            new TypeReference<Map<String, Map<String, String>>>() {});

            System.out.println("Availability map keys: " + availability.keySet()); // Debug log

            if (!availability.containsKey(day)) {
                System.out.println("Day not found in availability: " + day); // Debug log
                response.put("available", false);
                response.put("message", "Doctor not available on " + dayOfWeek);
                return response;
            }

            // Rest of the method remains the same...

            Map<String, String> dayAvailability = availability.get(day);

            // Handle both "start"/"end" and "end"/"start" orders
            String startTimeStr = dayAvailability.containsKey("start") ?
                    dayAvailability.get("start") :
                    dayAvailability.get("end"); // Fallback to "end" if "start" not found
            String endTimeStr = dayAvailability.containsKey("end") ?
                    dayAvailability.get("end") :
                    dayAvailability.get("start"); // Fallback to "start" if "end" not found

            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            // Ensure start is before end
            if (startTime.isAfter(endTime)) {
                // Swap them if they're in the wrong order
                LocalTime temp = startTime;
                startTime = endTime;
                endTime = temp;
            }

            // Generate 30-minute slots
            LocalTime current = startTime;
            while (current.isBefore(endTime)) {
                LocalTime slotEnd = current.plusMinutes(30);
                if (slotEnd.isAfter(endTime)) break;

                // Check if slot is already booked
                List<Appointment> existing = appointmentRepository
                        .findByDoctorAndAppointmentDateAndTimeSlot(
                                doctor,
                                appointmentDate,
                                current);

                boolean isAvailable = existing.isEmpty();

                slots.add(Map.of(
                        "time", current.toString(),
                        "end", slotEnd.toString(),
                        "available", isAvailable
                ));

                current = slotEnd;
            }

            response.put("availableSlots", slots);
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error checking availability: " + e.getMessage());
        }
    }
}