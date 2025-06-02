/*package com.example.PAMS.controller;

import com.example.PAMS.entities.Patient;
import com.example.PAMS.service.PatientRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientRegistrationService patientService;

    @Autowired
    public PatientController(PatientRegistrationService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);
        model.addAttribute("patient", patient);
        return "patient-dashboard";
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

        // Set the ID from the existing patient to ensure we're updating the right record
        patientDetails.setPatientId(existingPatient.getPatientId());
        patientDetails.setEmail(existingPatient.getEmail()); // Email shouldn't be changed

        patientService.updatePatientProfile(patientDetails);
        return "redirect:/patient/dashboard?success";
    }
}*/

package com.example.PAMS.controller;

import com.example.PAMS.entities.*;
import com.example.PAMS.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    // Existing methods...

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);
        model.addAttribute("patient", patient);

        return "patient-dashboard";
    }

    @GetMapping("/book-appointment")
    public String showBookAppointmentForm(Model model) {
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        model.addAttribute("appointment", new Appointment());

        return "book-appointment";
    }

    @PostMapping("/book-appointment")
    public String bookAppointment(@ModelAttribute Appointment appointment,
                                  Authentication authentication) {
        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);

        appointment.setPatient(patient);
        appointment.setStatus(Appointment.AppointmentStatus.BOOKED);

        if (appointmentService.isSlotAvailable(appointment)) {
            appointmentService.bookAppointment(appointment);
            return "redirect:/patient/dashboard?success=appointment_booked";
            //return "redirect:/patient/dashboard?success=Appointment+booked+successfully";
        } else {
            return "redirect:/patient/book-appointment?error=slot_not_available";
            //return "redirect:/patient/book-appointment?error=Slot+not+available.+Please+choose+another+time";
        }
    }



    @GetMapping("/appointments")
    public String viewAppointments(Authentication authentication, Model model) {
        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);
        List<Appointment> appointments = appointmentService.getPatientAppointments(patient);
        model.addAttribute("appointments", appointments);
        return "appointment-history";
    }

    @PostMapping("/cancel-appointment/{id}")
    public String cancelAppointment(@PathVariable Integer id) {
        appointmentService.cancelAppointment(id);
        return "redirect:/patient/appointments?success=appointment_canceled";
        //return "redirect:/patient/appointments?success=Appointment+canceled+successfully";
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

        // Set the ID from the existing patient to ensure we're updating the right record
        patientDetails.setPatientId(existingPatient.getPatientId());
        patientDetails.setEmail(existingPatient.getEmail()); // Email shouldn't be changed

        patientService.updatePatientProfile(patientDetails);
        return "redirect:/patient/dashboard?success";
        //return "redirect:/patient/dashboard?success=Profile+updated+successfully";
    }
}