package com.example.job.portal.Naukri4U.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "job")
public class JobEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @JsonProperty(value = "job_req_Id")
    @Column(unique = true, nullable = false)
    private String jobReqId;

    @JsonProperty(value = "added_by")
    @Column(nullable = false)
    private String addedBy;

    @JsonProperty(value = "job_title")
    @Column(unique = true, nullable = false)
    private String jobTitle;

    @JsonProperty(value = "company_name")
    @Column(nullable = false)
    private String companyName;

    @JsonProperty(value = "location")
    @Column(nullable = false)
    private String location;

    @JsonProperty(value = "posted_date")
    @Column(nullable = false)
    private String posted_date;

    @JsonProperty(value = "last_date_to_apply")
    @Column(nullable = false)
    private String last_date_to_apply;

    @JsonProperty(value = "min_salary")
    @Column(nullable = false)
    private Integer minSalary;

    @JsonProperty(value = "min_experience")
    @Column(nullable = false)
    private Integer minExperience;

    @JsonProperty(value = "max_experience")
    @Column(nullable = false)
    private Integer maxExperience;

    @JsonProperty(value = "max_salary")
    @Column(nullable = false)
    private Integer maxSalary;

    @Lob
    @JsonProperty(value = "job_description")
    private String job_description;

    @JsonProperty(value = "skills_required")
    @Column(nullable = false)
    private List<String> skillsRequired;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobReqId() {
        return jobReqId;
    }

    public void setJobReqId(String jobReqId) {
        this.jobReqId = jobReqId;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPosted_date() {
        return posted_date;
    }

    public void setPosted_date(String posted_date) {
        this.posted_date = posted_date;
    }

    public String getLast_date_to_apply() {
        return last_date_to_apply;
    }

    public void setLast_date_to_apply(String last_date_to_apply) {
        this.last_date_to_apply = last_date_to_apply;
    }

    public Integer getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Integer minSalary) {
        this.minSalary = minSalary;
    }

    public Integer getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(Integer minExperience) {
        this.minExperience = minExperience;
    }

    public Integer getMaxExperience() {
        return maxExperience;
    }

    public void setMaxExperience(Integer maxExperience) {
        this.maxExperience = maxExperience;
    }

    public Integer getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Integer maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getJob_description() {
        return job_description;
    }

    public void setJob_description(String job_description) {
        this.job_description = job_description;
    }

    public List<String> getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(List<String> skillsRequired) {
        this.skillsRequired = skillsRequired;
    }
}
