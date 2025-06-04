package com.example.PAMS.controller;

import com.example.PAMS.entities.Appointment;
import com.example.PAMS.entities.Doctor;
import com.example.PAMS.entities.Patient;
import com.example.PAMS.repository.AppointmentRepository;
import com.example.PAMS.repository.DoctorRepository;
import com.example.PAMS.repository.PatientRepository;
import com.example.PAMS.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/book/appointment")
    public String showBookAppointmentForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("doctors", doctorRepository.findAll());
        return "patient-book-appointment";
    }

    @PostMapping("/book/appointment")
    public String bookAppointment(
            @RequestParam("timeHour") String hour,
            @RequestParam("timeMinute") String minute,
            @ModelAttribute Appointment appointment,
            BindingResult result,
            Principal principal,
            Model model) {

        try {
            // Convert time parameters to LocalTime
            LocalTime timeSlot = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute));
            appointment.setTimeSlot(timeSlot);

            // Get patient by email (username)
            String email = principal.getName();
            Patient patient = patientRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            // Set patient and book appointment
            appointment.setPatient(patient);
            appointmentService.bookAppointment(appointment);

            return "redirect:/patient/dashboard?success=Appointment+booked+successfully";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("doctors", doctorRepository.findAll());
            return "patient-book-appointment";
        }
    }
}