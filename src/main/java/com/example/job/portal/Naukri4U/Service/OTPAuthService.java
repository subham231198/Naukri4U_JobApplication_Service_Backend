package com.example.job.portal.Naukri4U.Service;

import com.example.job.portal.Naukri4U.DTO.SessionHandler;
import com.example.job.portal.Naukri4U.Entity.OTPEntity;
import com.example.job.portal.Naukri4U.Entity.UserAuthEntity;
import com.example.job.portal.Naukri4U.Exceptions.AuthenticationFailedException;
import com.example.job.portal.Naukri4U.Exceptions.ProfileLockedException;
import com.example.job.portal.Naukri4U.Exceptions.SessionInvalidException;
import com.example.job.portal.Naukri4U.Repository.OtpRepo;
import com.example.job.portal.Naukri4U.Repository.UserAuthRepo;
import com.example.job.portal.Naukri4U.Utility.DataEncrypter;
import com.example.job.portal.Naukri4U.Utility.Generator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class OTPAuthService {

    @Autowired
    private Generator generator;

    @Autowired
    private PolicyDecisionPointService pdpService;

    @Autowired
    private OtpRepo otpRepo;

    @Autowired
    private DataEncrypter dataEncrypter;

    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private UserAuthRepo userAuthRepo;

    private static final Logger logger = LogManager.getLogger(OTPAuthService.class);

    public ResponseEntity<Map<String, Object>> getOTP(String tokenId, String csrf) throws Exception {
        Map<String, Object> sessionInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        Integer cam_level = (Integer) sessionInfo.get("authLevel");
        Map<String, Object> response = new LinkedHashMap<>();
        if(cam_level == 40)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile authLevel is 40!");
        }

        Optional<OTPEntity> getUser = otpRepo.findByUsername(sessionInfo.get("username").toString());
        if(!getUser.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }
        String otp = generator.GenerateOTP();
        OTPEntity entity = getUser.get();

        String previous_otp = entity.getOTP();
        Instant expiry = Instant.parse(entity.getExpiry_timeStamp());

        if(Instant.now().isBefore(expiry))
        {
            response.put("otp", dataEncrypter.decryptData(previous_otp));
            response.put("expiry", expiry);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Instant issuedAt = Instant.now();
        expiry = issuedAt.plusSeconds(30);
        if(otp != null)
        {
            entity.setOTP(dataEncrypter.encryptData(otp));
            entity.setCreated_timeStamp(issuedAt.toString());
            entity.setExpiry_timeStamp(expiry.toString());
        }
        otpRepo.save(entity);
        response.put("otp", otp);
        response.put("expiry", expiry);

        logger.info("Response {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<Map<String, Object>> validateOTP(String tokenId, String csrf, String OTP) throws Exception {
        Map<String, Object> sessionInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();
        Integer cam_level = (Integer) sessionInfo.get("authLevel");
        Map<String, Object> response = new LinkedHashMap<>();
        if(cam_level == 40)
        {
            response.put("sessionId", sessionInfo.get("sessionId"));
            response.put("expiryTime", sessionInfo.get("expiryTime"));

            logger.info("Response {}", response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Optional<OTPEntity> getUser = otpRepo.findByUsername(sessionInfo.get("username").toString());
        Optional<UserAuthEntity> entity = userAuthRepo.findByUsername(sessionInfo.get("username").toString());
        if(!getUser.isPresent() || !entity.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile already having auth level = 40");
        }
        UserAuthEntity userAuthEntity = entity.get();

        if(userAuthEntity.getIs_account_locked().equals("true"))
        {
            throw new ProfileLockedException("Profile "+userAuthEntity.getUsername()+" is locked!");
        }
        Integer counter = entity.get().getInvalid_login_attempts();

        String otp = dataEncrypter.decryptData(getUser.get().getOTP());
        Instant expiry = Instant.parse(getUser.get().getExpiry_timeStamp());
        if(otp.equals(OTP))
        {
            if(Instant.now().isAfter(expiry))
            {
                throw new AuthenticationFailedException("OTP has expired!");
            }

            Map<String, Object> Session = sessionHandler.getSessions().stream()
                    .filter(session -> session.get("sessionId").equals(tokenId))
                    .findFirst()
                    .orElseThrow(() -> new SessionInvalidException("Session "+tokenId+" is invalid or has expired"));
            Session.replace("cam_level", 40);
            Session.replace("expiry", Instant.now().plusSeconds(3600));

            userAuthEntity.setInvalid_login_attempts(0);
            userAuthEntity.setLast_login_time(Instant.now().toString());
            userAuthRepo.save(userAuthEntity);
            response.put("sessionId", Session.get("sessionId"));
            response.put("expiryTime", Session.get("expiryTime"));

            logger.info("Response {}", response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else
        {
            if(counter == 10)
            {
                throw new ProfileLockedException("User profile "+getUser.get().getUsername()+" is locked due to too many invalid otp attempts");
            }
            counter++;
            userAuthEntity.setInvalid_login_attempts(counter);
            userAuthRepo.save(userAuthEntity);
            throw new AuthenticationFailedException("Invalid OTP!");
        }
    }
}
