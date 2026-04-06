package com.example.job.portal.Naukri4U.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.HQLSelect;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_otp_entity")
public class OTPEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "username")
    @Column(unique = true, nullable = true)
    private String username;

    @JsonProperty(value = "email")
    private String email;

    @JsonProperty(value = "phone")
    private String phone;

    @JsonProperty(value = "otp")
    private String OTP;

    @JsonProperty(value = "issuedAt")
    private String created_timeStamp;

    @JsonProperty(value = "expiry")
    private String expiry_timeStamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }

    public String getCreated_timeStamp() {
        return created_timeStamp;
    }

    public void setCreated_timeStamp(String created_timeStamp) {
        this.created_timeStamp = created_timeStamp;
    }

    public String getExpiry_timeStamp() {
        return expiry_timeStamp;
    }

    public void setExpiry_timeStamp(String expiry_timeStamp) {
        this.expiry_timeStamp = expiry_timeStamp;
    }
}
