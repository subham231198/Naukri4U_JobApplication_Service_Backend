package com.example.job.portal.Naukri4U.Service;

import com.example.job.portal.Naukri4U.DTO.PendingRecruiterDTO;
import com.example.job.portal.Naukri4U.Entity.*;
import com.example.job.portal.Naukri4U.Exceptions.CompanyNotFoundException;
import com.example.job.portal.Naukri4U.Exceptions.UserAlreadyExistsException;
import com.example.job.portal.Naukri4U.Repository.*;
import com.example.job.portal.Naukri4U.Utility.DataEncrypter;
import org.apache.catalina.User;
import org.apache.coyote.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AdminService {

    @Autowired
    CompanyRepo companyRepo;

    @Autowired
    RecruiterRepo recruiterRepo;

    @Autowired
    CandidateRepo candidateRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserAuthRepo userAuthRepo;

    @Autowired
    PolicyDecisionPointService pdpService;

    @Autowired
    PendingApprovalRepo pending;

    @Autowired
    DataEncrypter dataEncrypter;

    @Autowired
    OtpRepo otpRepo;

    private static final Logger logger = LogManager.getLogger(AdminService.class);

    public ResponseEntity<List<UserEntity>> getAllUsers(String tokenId, String csrf) {

        Map<String, Object> sessionAttributes = pdpService.sessionAttributes(tokenId, csrf).getBody();

        userRepo.findByUsername(sessionAttributes.get("username").toString()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found!"));

        String role = (String) sessionAttributes.get("role");
        if (role == null || !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Integer cam_level = (Integer)sessionAttributes.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }

        return ResponseEntity.ok(userRepo.findAll());
    }

    public ResponseEntity<Map<String, Object>> deleteCompanyById(String tokenId, String csrf, Long companyId) {

        Map<String, Object> sessionAttributes = pdpService.sessionAttributes(tokenId, csrf).getBody();

        userRepo.findByUsername(sessionAttributes.get("username").toString()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found!"));

        String role = (String) sessionAttributes.get("role");
        if (role == null || !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Integer cam_level = (Integer)sessionAttributes.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }
        if (!companyRepo.existsById(companyId)) {
            throw new CompanyNotFoundException("Company with ID " + companyId + " not found.");
        }
        companyRepo.deleteById(companyId);
        return ResponseEntity.ok(Map.of("message", "Company with ID " + companyId + " has been deleted successfully."));
    }


    public ResponseEntity<Map<String, Object>> deleteRecruiterById(String tokenId, String csrf, Long recruiterId) {

        Map<String, Object> sessionAttributes = pdpService.sessionAttributes(tokenId, csrf).getBody();

        userRepo.findByUsername(sessionAttributes.get("username").toString()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found!"));

        String role = (String) sessionAttributes.get("role");
        if (role == null || !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Integer cam_level = (Integer)sessionAttributes.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }
        if (!recruiterRepo.existsById(recruiterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter with ID " + recruiterId + " not found.");
        }
        recruiterRepo.deleteById(recruiterId);
        return ResponseEntity.ok(Map.of("message", "Recruiter with ID " + recruiterId + " has been deleted successfully."));
    }

    public ResponseEntity<Map<String, Object>> deleteCandidateById(String tokenId, String csrf, Long candidateId) {

        Map<String, Object> sessionAttributes = pdpService.sessionAttributes(tokenId, csrf).getBody();

        userRepo.findByUsername(sessionAttributes.get("username").toString()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found!"));

        String role = (String) sessionAttributes.get("role");
        if (role == null || !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }
        if (!candidateRepo.existsById(candidateId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate with ID " + candidateId + " not found.");
        }
        candidateRepo.deleteById(candidateId);
        return ResponseEntity.ok(Map.of("message", "Candidate with ID " + candidateId + " has been deleted successfully."));
    }

    public ResponseEntity<Map<String, Object>> approveRecruiterById(String tokenId, String csrf, String recruiter_username) throws Exception {

        Map<String, Object> sessionAttributes = pdpService.sessionAttributes(tokenId, csrf).getBody();

        userRepo.findByUsername(sessionAttributes.get("username").toString()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found!"));

        String role = (String) sessionAttributes.get("role");
        if (role == null || !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Integer cam_level = (Integer)sessionAttributes.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }

        if (!recruiterRepo.findByUsername(recruiter_username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter with ID " + recruiter_username + " not found.");
        }

        Optional<UserAuthEntity> userAuthEntity = userAuthRepo.findByUsername(recruiter_username);
        UserEntity recruiterEntity = userRepo.findByUsername(recruiter_username).get();
        String email = dataEncrypter.decryptData(recruiterEntity.getEmail());
        String[] emailParts = email.split("@", 2);
        String host = emailParts[1];

        CompanyEntity companyEntity = companyRepo.findByDomain(host);
        if(companyEntity == null)
        {
            UserAuthEntity recruiterAuth = userAuthEntity.get();
            recruiterAuth.setAccount_status("Rejected");
            userAuthRepo.save(recruiterAuth);
            return ResponseEntity.ok(Map.of("message", "Recruiter with ID " + recruiter_username + " has been rejected due to invalid company domain."));
        }

        if (userAuthEntity.isPresent()) {
            if (userAuthEntity.get().getAccount_status().equals("Active")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recruiter with username " + recruiter_username + " is already approved.");
            }
            UserAuthEntity recruiterAuth = userAuthEntity.get();
            recruiterAuth.setAccount_status("Active");
            userAuthRepo.save(recruiterAuth);

            OTPEntity otpEntity = new OTPEntity();
            if(otpRepo.existsByEmail(recruiterEntity.getEmail()))
            {
                throw new UserAlreadyExistsException("User already exists by email: "+dataEncrypter.decryptData(recruiterEntity.getEmail()));
            }
            if(otpRepo.existsByPhone(recruiterEntity.getPhoneNumber()))
            {
                throw new UserAlreadyExistsException("User already exists by phone: "+dataEncrypter.decryptData(recruiterEntity.getPhoneNumber()));
            }

            otpEntity.setOTP("111111");
            otpEntity.setUsername(recruiter_username);
            otpEntity.setEmail(recruiterEntity.getEmail());
            otpEntity.setPhone(recruiterEntity.getPhoneNumber());
            otpEntity.setCreated_timeStamp("1111-01-01T11:11:11.111Z");
            otpEntity.setExpiry_timeStamp("1111-01-01T11:11:11.111Z");
            otpRepo.save(otpEntity);
            pending.deleteByUsername(recruiter_username);
        }
        return ResponseEntity.ok(Map.of("message", "Recruiter with ID " + recruiter_username + " has been approved successfully."));


    }

    public ResponseEntity<List<PendingRecruiterDTO>> getPendingRecruiters(String tokenId, String csrf) throws Exception {

        Map<String, Object> sessionAttributes = pdpService.sessionAttributes(tokenId, csrf).getBody();

        userRepo.findByUsername(sessionAttributes.get("username").toString())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found!"));

        String role = (String) sessionAttributes.get("role");
        if (role == null || !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Integer cam_level = (Integer)sessionAttributes.get("authLevel");
        if(cam_level != 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
        }

        List<PendingAccountApprovalQueue> pendingRecruiters = pending.findAll();

        List<PendingRecruiterDTO> response = pendingRecruiters.stream()
                .map(p -> {
                    try {
                        return new PendingRecruiterDTO(
                                p.getUsername(),
                                p.getRole(),
                                dataEncrypter.decryptData(p.getEmail()),
                                p.getCompanyName()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("Error decrypting email", e);
                    }
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> getUserInfo_admin(String tokenId, String csrf, String username)
    {
        Map<String, Object> sessionInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        String role = sessionInfo.get("role").toString();

        if(!role.equalsIgnoreCase("ADMIN"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can access!");
        }

        Optional<UserEntity> getUser = userRepo.findByUsername(username);
        if(!getUser.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("username", getUser.get().getUsername());
        response.put("role", getUser.get().getRole());
        response.put("location", getUser.get().getCurrent_location());

        logger.info("Response {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}