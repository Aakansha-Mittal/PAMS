package com.example.PAMS.repository;

import com.example.PAMS.entities.Admin;
import com.example.PAMS.entities.Doctor;
import com.example.PAMS.entities.Patient;
import com.example.PAMS.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPatient(Patient patient);

    Optional<User> findByDoctor(Doctor doctor);

    Optional<User> findByAdmin(Admin admin);

    Optional<User> findByPatient_PatientId(Integer patientId);
}
