package com.example.PAMS.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {
    @GetMapping("/dashboard")
    public String doctorDashboard(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "doctor-dashboard";
    }
}