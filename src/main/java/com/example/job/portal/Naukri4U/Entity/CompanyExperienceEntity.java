package com.example.job.portal.Naukri4U.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@Entity
//@Table(name = "candidate_experience_table")
public class CompanyExperienceEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "company_name")
    private String companyName;

    @JsonProperty(value = "designation")
    private String designation;

    @JsonProperty(value = "company_location")
    private String companyLocation;

    @JsonProperty(value = "start_date")
    private String startDate;

    @JsonProperty(value = "end_date")
    private String endDate;

    @JsonProperty(value = "is_current_company")
    private Boolean currentCompany;

    @JsonProperty(value = "keyResponsibilities")
    private String keyResponsibilities;


//    @ManyToOne
//    @JoinColumn(name = "username")
//    private CandidateEntity candidateEntity;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCompanyLocation() {
        return companyLocation;
    }

    public void setCompanyLocation(String companyLocation) {
        this.companyLocation = companyLocation;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Boolean getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(Boolean currentCompany) {
        this.currentCompany = currentCompany;
    }

    public String getKeyResponsibilities() {
        return keyResponsibilities;
    }

    public void setKeyResponsibilities(String keyResponsibilities) {
        this.keyResponsibilities = keyResponsibilities;
    }
}
