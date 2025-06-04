package com.example.PAMS.controller;

import com.example.PAMS.entities.Appointment;
import com.example.PAMS.entities.Doctor;
import com.example.PAMS.repository.AppointmentRepository;
import com.example.PAMS.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/dashboard")
    public String doctorDashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Appointment> todayAppointments = appointmentRepository
                .findByDoctorAndAppointmentDate(doctor, LocalDate.now());

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", todayAppointments);
        return "doctor-dashboard";
    }

    @GetMapping("/appointments")
    public String viewAppointments(Model model, Authentication authentication) {
        String email = authentication.getName();
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Appointment> appointments = appointmentRepository.findByDoctorOrderByAppointmentDateDescTimeSlotDesc(doctor);
        model.addAttribute("appointments", appointments);
        return "doctor-appointments";
    }

    @PostMapping("/appointments/complete/{id}")
    public String completeAppointment(@PathVariable Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        return "redirect:/doctor/appointments?completed=true";
    }
}