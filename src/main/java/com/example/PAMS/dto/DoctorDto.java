package com.example.PAMS.dto;

import lombok.Data;

@Data
public class DoctorDto {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String specialization;

    // Availability fields
    private String mondayStart;
    private String mondayEnd;
    private String tuesdayStart;
    private String tuesdayEnd;
    private String wednesdayStart;
    private String wednesdayEnd;
    private String thursdayStart;
    private String thursdayEnd;
    private String fridayStart;
    private String fridayEnd;
    private String saturdayStart;
    private String saturdayEnd;
    private String sundayStart;
    private String sundayEnd;

}