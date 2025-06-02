package com.example.PAMS.service;

import com.example.PAMS.entities.Patient;
import com.example.PAMS.entities.User;
import com.example.PAMS.repository.PatientRepository;
import com.example.PAMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PatientRegistrationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    public void registerPatient(Patient patient) {
        // Encrypt password
        String encodedPassword = passwordEncoder.encode(patient.getPassword());
        patient.setPassword(encodedPassword);

        // Save patient
        Patient savedPatient = patientRepository.save(patient);

        // Create user
        User user = new User();
        user.setEmail(patient.getEmail());
        user.setPassword(encodedPassword);
        user.setRole("PATIENT");
        user.setPatient(savedPatient);

        userRepository.save(user);
    }

    // Similar methods for doctor/admin registration
}
