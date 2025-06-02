package com.example.PAMS.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer patientId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate dob;
    private String password;

    @OneToOne(mappedBy = "patient")
    private User user;
}




