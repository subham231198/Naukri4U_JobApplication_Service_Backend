package com.example.job.portal.Naukri4U.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "application")
public class ApplicationEntity
{
    @Id
    @GeneratedValue
    private Long id;

    @JsonProperty(value = "job_req_id")
    @Column(nullable = false)
    private String jobReqId;

    @JsonProperty(value = "username")
    @Column(nullable = false)
    private String username;

    @JsonProperty(value = "applied_date")
    @Column(nullable = false)
    private String appliedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobReqId() {
        return jobReqId;
    }

    public void setJobReqId(String jobReqId) {
        this.jobReqId = jobReqId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }
}
