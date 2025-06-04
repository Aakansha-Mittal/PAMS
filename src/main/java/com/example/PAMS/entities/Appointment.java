package com.example.PAMS.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer appointmentId;

    @ManyToOne
    @JoinColumn(name = "patientId")
    private Patient patient;

    @NotNull(message = "Please select a doctor")
    @ManyToOne
    @JoinColumn(name = "doctorId")
    private Doctor doctor;

    @NotNull(message = "Please select a date")
    private LocalDate appointmentDate;

    @NotNull(message = "Please select a time")
    private LocalTime timeSlot;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    public enum AppointmentStatus {
        BOOKED, CANCELED, COMPLETED
    }
}