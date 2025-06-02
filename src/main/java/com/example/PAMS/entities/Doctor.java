package com.example.PAMS.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer doctorId;
    private String name;
    private String specialization;
    private String email;
    private String phone;
    private String password;

    @Column(name = "availability", columnDefinition = "JSON")
    private String availability;

    @OneToOne(mappedBy = "doctor")
    private User user;
}
