package com.example.job.portal.Naukri4U.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingRecruiterDTO {
    private String username;
    private String role;
    private String email;
    private String companyName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public PendingRecruiterDTO(String username, String role, String email, String companyName) {
        this.username = username;
        this.role = role;
        this.email = email;
        this.companyName = companyName;
    }


}