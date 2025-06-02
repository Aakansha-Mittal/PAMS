package com.example.PAMS.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

// Role-Specific Controllers
@Controller
@RequestMapping("/patient")
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    @GetMapping("/dashboard")
    public String patientDashboard(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "patient-dashboard";
    }
}