package com.example.job.portal.Naukri4U.Service;

import com.example.job.portal.Naukri4U.Entity.CompanyEntity;
import com.example.job.portal.Naukri4U.Entity.JobEntity;
import com.example.job.portal.Naukri4U.Entity.QuarrentineJobEntity;
import com.example.job.portal.Naukri4U.Entity.UserEntity;
import com.example.job.portal.Naukri4U.Exceptions.CompanyNotFoundException;
import com.example.job.portal.Naukri4U.Exceptions.InsufficientJobInfoException;
import com.example.job.portal.Naukri4U.Exceptions.QuarrentineJobInfoException;
import com.example.job.portal.Naukri4U.Repository.CompanyRepo;
import com.example.job.portal.Naukri4U.Repository.JobRepo;
import com.example.job.portal.Naukri4U.Repository.QuarrentineJobRepo;
import com.example.job.portal.Naukri4U.Repository.UserRepo;
import com.example.job.portal.Naukri4U.Utility.DataEncrypter;
import com.example.job.portal.Naukri4U.Utility.FraudJobDetection;
import com.example.job.portal.Naukri4U.Utility.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class JobService
{
    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PolicyDecisionPointService pdpService;

    @Autowired
    private Generator generator;

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private DataEncrypter dataEncrypter;

    @Autowired
    private QuarrentineJobRepo quarrentineJobRepo;

    @Autowired
    private FraudJobDetection fraudJobDetection;

    public ResponseEntity<Map<String, Object>> addJob(String tokenId, String csrf, JobEntity jobEntity) throws Exception {
        Map<String, Object> sessionInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        assert sessionInfo != null;
        String username = sessionInfo.get("username").toString();
        String role = sessionInfo.get("role").toString();

        if(!role.equalsIgnoreCase("RECRUITER"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only recruiters can add jobs");
        }
        Integer cam_level = (Integer)sessionInfo.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }
        jobEntity.setAddedBy(username);

        Optional<UserEntity> entity = userRepo.findByUsername(username);
        if(!entity.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Recruiter not found by username");
        }
        String email = dataEncrypter.decryptData(entity.get().getEmail());
        String[] emailParts = email.split("@", 2);
        CompanyEntity companyEntity  = companyRepo.findByDomain(emailParts[1]);
        if(companyEntity == null)
        {
            throw new CompanyNotFoundException("Company not found!");
        }
        String companyName = companyEntity.getCompanyName();
        jobEntity.setCompanyName(companyName);
        String jobReqId = generator.generateJobReqID();
        Optional<JobEntity> existingJob = jobRepo.findByJobReqId(jobReqId);
        while (existingJob.isPresent()) {
            jobReqId = generator.generateJobReqID();
            existingJob = jobRepo.findByJobReqId(jobReqId);
        }

        jobEntity.setJobReqId(jobReqId);

        if(jobEntity.getCompanyName() == null || jobEntity.getCompanyName().isEmpty() ||
                jobEntity.getJobTitle() == null || jobEntity.getJobTitle().isEmpty() ||
                jobEntity.getLocation() == null || jobEntity.getLocation().isEmpty() ||
                jobEntity.getMinSalary() == null || jobEntity.getMaxSalary() == null)
        {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required"));
        }

        List<String> requiredSkills = jobEntity.getSkillsRequired();
        if(requiredSkills == null || requiredSkills.isEmpty())
        {
            return ResponseEntity.badRequest().body(Map.of("message", "At least one skill is required"));
        }

        if(jobEntity.getMinSalary() <= 0 || jobEntity.getMaxSalary() <= 0)
        {
            return ResponseEntity.badRequest().body(Map.of("message", "Salary cannot be 0 or negative"));
        }

        if(jobEntity.getMinExperience() > jobEntity.getMaxExperience())
        {
            return ResponseEntity.badRequest().body(Map.of("message", "Minimum experience cannot be greater than maximum experience"));
        }

        if (jobEntity.getMinSalary() > jobEntity.getMaxSalary())
        {
            return ResponseEntity.badRequest().body(Map.of("message", "Minimum salary cannot be greater than maximum salary"));
        }

        if (jobEntity.getJob_description() == null || jobEntity.getJob_description().isEmpty())
        {
            return ResponseEntity.badRequest().body(Map.of("message", "Job description is required"));
        }


        jobEntity.setPosted_date(Instant.now().toString());
        jobEntity.setLast_date_to_apply(Instant.now().plus(10, ChronoUnit.DAYS).toString());
        if(!fraudJobDetection.validateJobDescription(jobEntity.getJob_description(), jobEntity.getSkillsRequired()))
        {
            throw new InsufficientJobInfoException("Job Info is not matching required skill set!");
        }

        jobRepo.save(jobEntity);
        if(fraudJobDetection.fraudJobDetection(jobEntity.getJob_description()) || fraudJobDetection.fraudJobDetection(jobEntity.getJobTitle()))
        {
            QuarrentineJobEntity quarrentineJob = new QuarrentineJobEntity();
            quarrentineJob.setJobReqId(jobReqId);
            quarrentineJob.setAddedBy(jobEntity.getAddedBy());
            quarrentineJob.setQuarentineTimeStamp(Instant.now().toString());
            quarrentineJobRepo.save(quarrentineJob);
            return new ResponseEntity<>(Map.of("message", "Job info is quarantined due to security concerns! Job Req ID: " + jobEntity.getJobReqId()), HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(Map.of("message", "Job added successfully! Job Req ID: " + jobEntity.getJobReqId()), HttpStatus.CREATED);
    }

    public ResponseEntity<List<JobEntity>> getAllJobs(String tokenId, String csrf)
    {
        Map<String, Object> sessionInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        String role = sessionInfo.get("role").toString();
        if(!role.equalsIgnoreCase("CANDIDATE") && !role.equalsIgnoreCase("ADMIN"))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        List<JobEntity> jobs = jobRepo.findAll();
        if(jobs.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No job found!");
        }
        List<JobEntity> result = new ArrayList<>();

        for (JobEntity e : jobs) {
            String jobReqId = e.getJobReqId();
            if (!quarrentineJobRepo.existsByJobReqId(jobReqId)) {
                result.add(e);
            }
        }
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<JobEntity> getJobById(String tokenId, String csrf, String jobReqId)
    {
        Map<String, Object> sessionInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        String username = sessionInfo.get("username").toString();
        String role = sessionInfo.get("role").toString();

        Integer cam_level = (Integer)sessionInfo.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }


        Optional<JobEntity> jobEntity = jobRepo.findByJobReqId(jobReqId);
        if(jobEntity.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No job found with the given Job Req ID = "+ jobReqId);
        }
        if(role.equalsIgnoreCase("RECRUITER"))
        {
            if(!jobEntity.get().getAddedBy().equalsIgnoreCase(username))
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
            }
        }
        if(!quarrentineJobRepo.existsByJobReqId(jobReqId))
        {
            return ResponseEntity.ok().body(jobEntity.get());
        }
        throw new QuarrentineJobInfoException("Job by id "+jobReqId+" is quarantined!");
    }

    public ResponseEntity<List<JobEntity>> getJobsByCompanyName(String tokenId, String csrf, String companyName)
    {
        Map<String, Object> sessionInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        if (sessionInfo == null || sessionInfo.get("role") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session!");
        }

        String role = sessionInfo.get("role").toString();

        if (!role.equalsIgnoreCase("CANDIDATE") && !role.equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Integer cam_level = (Integer)sessionInfo.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }

        CompanyEntity companyEntity = companyRepo.findByCompanyName(companyName);
        if (companyEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No company found with the given company name = " + companyName);
        }

        List<JobEntity> jobEntities = jobRepo.findByCompanyName(companyName);

        if (jobEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No job found for the given company name = " + companyName);
        }

        List<JobEntity> result = new ArrayList<>();

        for (JobEntity e : jobEntities) {
            String jobReqId = e.getJobReqId();
            if (!quarrentineJobRepo.existsByJobReqId(jobReqId)) {
                result.add(e);
            }
        }
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<List<JobEntity>> getJobsByLocation(String tokenId, String csrf, String location)
    {
        Map<String, Object> sessionInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        if (sessionInfo == null || sessionInfo.get("role") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session!");
        }

        String role = sessionInfo.get("role").toString();

        if (!role.equalsIgnoreCase("CANDIDATE") && !role.equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }


        Integer cam_level = (Integer)sessionInfo.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }

        List<JobEntity> jobEntities = jobRepo.findByLocation(location);

        if (jobEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No job found for the given location = " + location);
        }

        List<JobEntity> result = new ArrayList<>();

        for (JobEntity e : jobEntities) {
            String jobReqId = e.getJobReqId();
            if (!quarrentineJobRepo.existsByJobReqId(jobReqId)) {
                result.add(e);
            }
        }

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<List<JobEntity>> getJobsBySalaryRange(String tokenId, String csrf,
                                                                Integer expectedMin, Integer expectedMax)
    {
        Map<String, Object> sessionInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        if (sessionInfo == null || sessionInfo.get("role") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session!");
        }

        String role = sessionInfo.get("role").toString();
        Integer cam_level = (Integer)sessionInfo.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }

        if (!role.equalsIgnoreCase("CANDIDATE") && !role.equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        if (expectedMin == null || expectedMax == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary range cannot be null!");
        }

        if (expectedMin > expectedMax) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid salary range: min salary cannot be greater than max salary");
        }

        List<JobEntity> jobEntities =
                jobRepo.findByMinSalaryLessThanEqualAndMaxSalaryGreaterThanEqual(expectedMax, expectedMin);

        if (jobEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No job found for the given salary range: " + expectedMin + " - " + expectedMax);
        }

        List<JobEntity> result = new ArrayList<>();

        for (JobEntity e : jobEntities) {
            String jobReqId = e.getJobReqId();
            if (!quarrentineJobRepo.existsByJobReqId(jobReqId)) {
                result.add(e);
            }
        }

        return ResponseEntity.ok(result);
    }


    public ResponseEntity<List<JobEntity>> getJobsByExperienceRange(String tokenId, String csrf,
                                                                Integer expectedMin, Integer expectedMax)
    {
        Map<String, Object> sessionInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        if (sessionInfo == null || sessionInfo.get("role") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session!");
        }

        String role = sessionInfo.get("role").toString();

        if (!role.equalsIgnoreCase("CANDIDATE") && !role.equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Integer cam_level = (Integer)sessionInfo.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }

        if (expectedMin == null || expectedMax == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Experience range cannot be null!");
        }

        if (expectedMin > expectedMax) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid experience range: min experience cannot be greater than max experience");
        }

        List<JobEntity> jobEntities =
                jobRepo.findByMinExperienceLessThanEqualAndMaxExperienceGreaterThanEqual(expectedMax, expectedMin);

        if (jobEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No job found for the given experience range: " + expectedMin + " - " + expectedMax);
        }

        List<JobEntity> result = new ArrayList<>();

        for (JobEntity e : jobEntities) {
            String jobReqId = e.getJobReqId();
            if (!quarrentineJobRepo.existsByJobReqId(jobReqId)) {
                result.add(e);
            }
        }

        return ResponseEntity.ok(result);
    }
}
