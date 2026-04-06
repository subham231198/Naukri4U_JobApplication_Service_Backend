package com.example.job.portal.Naukri4U.Service;

import com.example.job.portal.Naukri4U.DTO.CandidateDTO;
import com.example.job.portal.Naukri4U.Entity.ApplicationEntity;
import com.example.job.portal.Naukri4U.Entity.CandidateEntity;
import com.example.job.portal.Naukri4U.Entity.UserEntity;
import com.example.job.portal.Naukri4U.Exceptions.ApplicationAlreadyExisits;
import com.example.job.portal.Naukri4U.Exceptions.CandidateNotFoundException;
import com.example.job.portal.Naukri4U.Exceptions.NoCandidateAgainstApplicationException;
import com.example.job.portal.Naukri4U.Repository.ApplicationRepo;
import com.example.job.portal.Naukri4U.Repository.CandidateRepo;
import com.example.job.portal.Naukri4U.Repository.UserRepo;
import com.example.job.portal.Naukri4U.Utility.DataEncrypter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private PolicyDecisionPointService pdpService;

    @Autowired
    private CandidateRepo candidateRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DataEncrypter dataEncrypter;

    public ResponseEntity<Map<String, Object>> apply(String tokenId, String csrf, String jobReqId)
    {
        Map<String, Object> sessionsInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        String username = sessionsInfo.get("username").toString();
        String role = sessionsInfo.get("role").toString();

        if(!role.equals("CANDIDATE"))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Integer cam_level = (Integer)sessionsInfo.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }

        if(applicationRepo.existsByUsernameAndJobReqId(username, jobReqId))
        {
            throw new ApplicationAlreadyExisits("Application already exists for candidate ID = "+username);
        }

        Optional<CandidateEntity> candidate = candidateRepo.findByUsername(username);
        if(!candidate.isPresent())
        {
            throw new CandidateNotFoundException("Candidate not found by username = "+username);
        }

        String current_company = candidate.get().getOrg_name();
        String designation = candidate.get().getDesignation();
        Integer YOE = candidate.get().getExperience();
        Integer salary = candidate.get().getSalary();
        List<String> skills = candidate.get().getSkills();
        if(current_company.isEmpty() || current_company==null
                || current_company.equals("") || designation.isEmpty()
                || designation == null || YOE == -1 || salary == -1 || skills.contains("TBD"))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Candidate yet to provide career info!");
        }


        ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setJobReqId(jobReqId);
        applicationEntity.setUsername(username);
        applicationEntity.setAppliedDate(Instant.now().toString());

        applicationRepo.save(applicationEntity);

        return new ResponseEntity<>(Map.of("message","Successfully applied to JOB Id ="+jobReqId), HttpStatus.CREATED);
    }


    public ResponseEntity<List<CandidateDTO>> getAllCandidatesByJobId(String tokenId, String csrf, String jobId) throws Exception {
        Map<String, Object> sessionsInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        String role = sessionsInfo.get("role").toString();

        if (!role.equalsIgnoreCase("RECRUITER")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Integer cam_level = (Integer)sessionsInfo.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }

        if (jobId == null || jobId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JobId cannot be empty!");
        }

        List<ApplicationEntity> applications = applicationRepo.findByJobReqId(jobId);

        if (applications.isEmpty()) {
            throw new NoCandidateAgainstApplicationException(
                    "No applicants found against Job Req Id = " + jobId);
        }

        List<CandidateDTO> applicants = new ArrayList<>();

        for (ApplicationEntity e : applications)
        {
            String username = e.getUsername();

            Optional<UserEntity> userOpt = userRepo.findByUsername(username);
            Optional<CandidateEntity> candidateOpt = candidateRepo.findByUsername(username);

            if (userOpt.isPresent() && candidateOpt.isPresent())
            {
                UserEntity user = userOpt.get();
                CandidateEntity candidate = candidateOpt.get();

                CandidateDTO dto = new CandidateDTO();
                dto.setUsername(user.getUsername());
                dto.setFirstName(user.getFirstName());
                dto.setLastName(user.getLastName());
                dto.setEmail(dataEncrypter.decryptData(user.getEmail()));
                dto.setPhone(dataEncrypter.decryptData(user.getPhoneNumber()));
                dto.setExperience(candidate.getExperience());
                dto.setCurrent_company(candidate.getOrg_name());
                dto.setDesignation(candidate.getDesignation());
                dto.setCurrent_salary(candidate.getSalary());
                dto.setSkillSet(candidate.getSkills());
                dto.setCurrentLocation(user.getCurrent_location());

                applicants.add(dto);
            }
        }

        return ResponseEntity.ok(applicants);
    }
}
