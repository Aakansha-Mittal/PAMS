package com.example.PAMS.dto;

import lombok.Data;

@Data
public class AdminDto {
    private Integer adminId;
    private String name;
    private String email;  // Add this field
    private String role;

    public AdminDto(Integer adminId, String name, String email, String role) {
        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}