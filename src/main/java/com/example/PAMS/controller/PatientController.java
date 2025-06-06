package com.example.PAMS.controller;

import com.example.PAMS.entities.*;
import com.example.PAMS.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientRegistrationService patientService;
    private final DoctorRegistrationService doctorService;
    private final AppointmentService appointmentService;

    @Autowired
    public PatientController(PatientRegistrationService patientService,
                             DoctorRegistrationService doctorService,
                             AppointmentService appointmentService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);
        model.addAttribute("patient", patient);
        return "patient-dashboard";
    }

    /*@GetMapping("/book/appointment")
    public String showBookAppointmentForm(Model model) {
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        model.addAttribute("appointment", new Appointment());
        return "patient-book-appointment";
    }


    @PostMapping("/book/appointment")
    public String bookAppointment(
            @ModelAttribute("appointment") Appointment appointment,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            // Basic validation
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Please fill all required fields correctly");
                return "redirect:/patient/book/appointment";
            }

            String email = authentication.getName();
            Patient patient = patientService.findByEmail(email);

            // Validate date is not in past
            if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("error", "Cannot book appointments in the past");
                return "redirect:/patient/book/appointment";
            }

            // Set patient and book appointment
            appointment.setPatient(patient);
            appointmentService.bookAppointment(appointment);

            redirectAttributes.addFlashAttribute("success", "Appointment booked successfully for " +
                    appointment.getAppointmentDate() + " at " + appointment.getTimeSlot());
            return "redirect:/patient/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/book/appointment";
        }
    }*/
    @GetMapping("/appointment/history")
    public String viewAppointments(Authentication authentication, Model model) {
        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);
        List<Appointment> appointments = appointmentService.getPatientAppointments(patient);
        model.addAttribute("appointments", appointments);
        return "patient-appointment-history";
    }

    @PostMapping("/cancel-appointment/{id}")
    public String cancelAppointment(@PathVariable Integer id, Authentication authentication) {
        appointmentService.cancelAppointment(id, authentication.getName());
        return "redirect:/patient/appointment/history?success=appointment_canceled";
    }


    @GetMapping("/profile")
    public String showProfileForm(Authentication authentication, Model model) {
        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);
        model.addAttribute("patient", patient);
        return "patient-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("patient") Patient patientDetails,
                                Authentication authentication) {
        String email = authentication.getName();
        Patient existingPatient = patientService.findByEmail(email);

        patientDetails.setPatientId(existingPatient.getPatientId());
        patientDetails.setEmail(existingPatient.getEmail());

        patientService.updatePatientProfile(patientDetails);
        return "redirect:/patient/dashboard?success";
    }
}