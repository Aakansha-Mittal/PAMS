package com.example.PAMS.controller;

import com.example.PAMS.dto.DoctorDto;
import com.example.PAMS.entities.Admin;
import com.example.PAMS.entities.Patient;
import com.example.PAMS.service.AdminRegistrationService;
import com.example.PAMS.service.DoctorRegistrationService;
import com.example.PAMS.service.PatientRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    @Autowired
    private PatientRegistrationService patientRegistrationService;

    @Autowired
    private DoctorRegistrationService doctorRegistrationService;

    @Autowired
    private AdminRegistrationService adminRegistrationService;

    // Patient Registration
    @GetMapping("/register/patient")
    public String showPatientRegistration(Model model) {
        model.addAttribute("patient", new Patient());
        return "patient-register";
    }

    @PostMapping("/register/patient")
    public String registerPatient(@Valid @ModelAttribute("patient") Patient patient,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "patient-register";
        }
        patientRegistrationService.registerPatient(patient);
        System.out.println(patient.getPassword());
        return "redirect:/login?registered";
    }

    // Doctor Registration
    @GetMapping("/register/doctor")
    public String showDoctorRegistration(Model model) {
        model.addAttribute("doctor", new DoctorDto());
        return "doctor-register";
    }

    @PostMapping("/register/doctor")
    public String registerDoctor(@Valid @ModelAttribute("doctor") DoctorDto doctorDto,
                                  BindingResult result) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "doctor-register";
        }
        doctorRegistrationService.registerDoctor(doctorDto);
        System.out.println(doctorDto.getPassword());
        return "redirect:/login?registered";
    }

    //Admin Registration
    @GetMapping("/register/admin")
    public String showAdminRegistration(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin-register";
    }

    @PostMapping("/register/admin")
    public String registerAdmin(@Valid @ModelAttribute("admin") Admin admin,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "admin-register";
        }
        adminRegistrationService.registerAdmin(admin);
        System.out.println(admin.getPassword());
        return "redirect:/login?registered";
    }
}


