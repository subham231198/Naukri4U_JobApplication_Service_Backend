package com.example.job.portal.Naukri4U.Entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "candidate")
public class CandidateEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "designation")
    private String designation;

    @JsonProperty(value = "organization")
    private String org_name;

    @JsonProperty(value = "salary")
    private Integer salary;

    @JsonProperty(value = "yoe")
    @JsonAlias("experience")
    private Integer experience;

    @JsonProperty(value = "resume")
    private String resume_path;

//    @OneToMany(mappedBy = "candidateEntity", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonProperty("company_list")
//    private List<CompanyExperienceEntity> companyExperienceEntity;

    @JsonProperty(value = "skills")
    private List<String> skills;

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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getResume_path() {
        return resume_path;
    }

    public void setResume_path(String resume_path) {
        this.resume_path = resume_path;
    }
//
//    public List<CompanyExperienceEntity> getCompanyExperienceEntity() {
//        return companyExperienceEntity;
//    }
//
//    public void setCompanyExperienceEntity(List<CompanyExperienceEntity> companyExperienceEntity) {
//        this.companyExperienceEntity = companyExperienceEntity;
//    }
}
