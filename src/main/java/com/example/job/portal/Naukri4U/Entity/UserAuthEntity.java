package com.example.job.portal.Naukri4U.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_auth_entity")
public class UserAuthEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "username")
    @Column(unique = true, nullable = false)
    private String username;

    @JsonProperty(value = "invalid_login_attempts")
    private Integer invalid_login_attempts;

    @JsonProperty(value = "is_account_locked")
    private String is_account_locked;

    @JsonProperty(value = "account_lock_time")
    private String account_lock_time;

    @JsonProperty(value = "is_profile_suspended")
    private String isProfileSuspended;

    @JsonProperty(value = "suspension_reason")
    private String suspension_reason;

    @JsonProperty(value = "last_login_time")
    private String last_login_time;

    @JsonProperty(value = "account_status")
    private String account_status;


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

    public Integer getInvalid_login_attempts() {
        return invalid_login_attempts;
    }

    public void setInvalid_login_attempts(Integer invalid_login_attempts) {
        this.invalid_login_attempts = invalid_login_attempts;
    }

    public String getIs_account_locked() {
        return is_account_locked;
    }

    public void setIs_account_locked(String is_account_locked) {
        this.is_account_locked = is_account_locked;
    }

    public String getAccount_lock_time() {
        return account_lock_time;
    }

    public void setAccount_lock_time(String account_lock_time) {
        this.account_lock_time = account_lock_time;
    }

    public String getIsProfileSuspended() {
        return isProfileSuspended;
    }

    public void setIsProfileSuspended(String isProfileSuspended) {
        this.isProfileSuspended = isProfileSuspended;
    }

    public String getSuspension_reason() {
        return suspension_reason;
    }

    public void setSuspension_reason(String suspension_reason) {
        this.suspension_reason = suspension_reason;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getAccount_status() {
        return account_status;
    }

    public void setAccount_status(String account_status) {
        this.account_status = account_status;
    }
}
