package com.example.PAMS.repository;

import com.example.PAMS.entities.Appointment;
import com.example.PAMS.entities.Doctor;
import com.example.PAMS.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByDoctorAndAppointmentDateAndTimeSlot(Doctor doctor, LocalDate date, LocalTime time);
    List<Appointment> findByPatientOrderByAppointmentDateDescTimeSlotDesc(Patient patient);

    List<Appointment> findByAppointmentDate(LocalDate today);
}