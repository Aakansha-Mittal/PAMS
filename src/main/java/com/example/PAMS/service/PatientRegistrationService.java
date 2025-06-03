package com.example.PAMS.service;

import com.example.PAMS.entities.Patient;
import com.example.PAMS.entities.User;
import com.example.PAMS.repository.PatientRepository;
import com.example.PAMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Patient findByEmail(String email) {
        Optional<Patient> patient = patientRepository.findByEmail(email);
        return patient.orElseThrow(() -> new RuntimeException("Patient not found with email: " + email));
    }

    public Patient findById(Integer patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
    }

    public Patient updatePatientProfile(Patient patientDetails) {
        Patient patient = findById(patientDetails.getPatientId());

        patient.setName(patientDetails.getName());
        patient.setPhone(patientDetails.getPhone());
        patient.setAddress(patientDetails.getAddress());
        patient.setDob(patientDetails.getDob());

        // Only update password if it's provided
        if (patientDetails.getPassword() != null && !patientDetails.getPassword().isEmpty()) {
            patient.setPassword(passwordEncoder.encode(patientDetails.getPassword()));
        }

        return patientRepository.save(patient);
    }
}