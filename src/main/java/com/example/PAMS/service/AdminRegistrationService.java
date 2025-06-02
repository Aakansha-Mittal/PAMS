package com.example.PAMS.service;

import com.example.PAMS.entities.Admin;
import com.example.PAMS.entities.Patient;
import com.example.PAMS.entities.User;
import com.example.PAMS.repository.AdminRepository;
import com.example.PAMS.repository.PatientRepository;
import com.example.PAMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminRegistrationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    public void registerAdmin(Admin admin) {
        // Encrypt password
        String encodedPassword = passwordEncoder.encode(admin.getPassword());
        admin.setPassword(encodedPassword);

        // Save Admin
        Admin savedAdmin = adminRepository.save(admin);

        // Create user
        User user = new User();
        user.setEmail(admin.getEmail());
        user.setPassword(encodedPassword);
        user.setRole("ADMIN");
        user.setAdmin(savedAdmin);

        userRepository.save(user);
    }
}
