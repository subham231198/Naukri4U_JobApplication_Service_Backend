package com.example.job.portal.Naukri4U.Service;

import com.example.job.portal.Naukri4U.Entity.CandidateEntity;
import com.example.job.portal.Naukri4U.Exceptions.CandidateNotFoundException;
import com.example.job.portal.Naukri4U.Repository.CandidateRepo;
import com.example.job.portal.Naukri4U.Repository.UserRepo;
import com.sun.net.httpserver.HttpsServer;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CandidateService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CandidateRepo candidateRepo;

    @Autowired
    private PolicyDecisionPointService pdpService;

    private static final Logger logger = LogManager.getLogger(CandidateService.class);

    public ResponseEntity<Map<String, Object>> updateCandidateInfo(String tokenId, String csrf, CandidateEntity candidate) {

        Map<String, Object> sessionsInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        String role = (String) sessionsInfo.get("role");
        if (role == null || !role.equalsIgnoreCase("CANDIDATE")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Integer cam_level = (Integer) sessionsInfo.get("authLevel");
        if (cam_level == null || cam_level != 40) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = " + cam_level);
        }

        String username = (String) sessionsInfo.get("username");
        if (username == null || username.isBlank()) {
            logger.error("Username not found in session = " + tokenId);
            return new ResponseEntity<>(Map.of("valid", false), HttpStatus.OK);
        }

        CandidateEntity candidateEntity = candidateRepo.findByUsername(username)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate not found = " + username));


        if (candidate.getSkills() != null && !candidate.getSkills().isEmpty()) {
            if (candidateEntity.getSkills() == null) {
                candidateEntity.setSkills(new ArrayList<>());
            }
            else {
                candidateEntity.getSkills().clear();
            }
            candidateEntity.getSkills().addAll(candidate.getSkills());
        }

        if (candidate.getOrg_name() != null && !candidate.getOrg_name().isBlank()) {
            candidateEntity.setOrg_name(candidate.getOrg_name());
        }

        if (candidate.getDesignation() != null && !candidate.getDesignation().isBlank()) {
            candidateEntity.setDesignation(candidate.getDesignation());
        }

        if (candidate.getExperience() != null && candidate.getExperience() >= 0) {
            candidateEntity.setExperience(candidate.getExperience());
        }

        if (candidate.getResume_path() != null) {
            candidateEntity.setResume_path(candidate.getResume_path());
        }

        if (candidate.getSalary() != null && candidate.getSalary() >= 0) {
            candidateEntity.setSalary(candidate.getSalary());
        }

        if (candidate.getUsername() != null && !candidate.getUsername().equals(username)) {
            logger.warn("Username change attempt! Old = {}, New = {}", username, candidate.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username cannot be changed!");
        }

        candidateRepo.save(candidateEntity);

        return new ResponseEntity<>(
                Map.of("message", "Details successfully updated for candidate = " + username),
                HttpStatus.CREATED
        );
    }
}
