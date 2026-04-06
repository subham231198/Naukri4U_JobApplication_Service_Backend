package com.example.job.portal.Naukri4U.Service;

import com.example.job.portal.Naukri4U.DTO.SessionHandler;
import com.example.job.portal.Naukri4U.Entity.UserAuthEntity;
import com.example.job.portal.Naukri4U.Entity.UserEntity;
import com.example.job.portal.Naukri4U.Exceptions.UserRejectException;
import com.example.job.portal.Naukri4U.Repository.UserAuthRepo;
import com.example.job.portal.Naukri4U.Repository.UserRepo;
import com.example.job.portal.Naukri4U.Utility.DataEncrypter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private DataEncrypter dataEncrypter;

    @Autowired
    private UserAuthRepo userAuthRepo;

    @Autowired
    private PolicyDecisionPointService policyDecisionPointService;

    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    public ResponseEntity<Map<String, Object>> login_email_password(String csrf, String email, String password, String correlationId, String channel) throws Exception {

        Optional<UserEntity> emailUser = userRepo.findByEmail(dataEncrypter.encryptData(email));
        if(!emailUser.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email address!");
        }

        Optional<UserEntity> userReal = userRepo.findByEmailAndPassword(dataEncrypter.encryptData(email), dataEncrypter.encryptData(password));
        String username = emailUser.get().getUsername();
        Optional<UserAuthEntity> userAuthEntity = userAuthRepo.findByUsername(username);
        if(!userAuthEntity.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User authentication details not found!");
        }
        if(!userReal.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password!");
        }

        String accountStatus = userAuthEntity.get().getAccount_status();
        String isProfileLocked = userAuthEntity.get().getIs_account_locked();
        String isProfileSuspended = userAuthEntity.get().getIsProfileSuspended();
        if(!accountStatus.equalsIgnoreCase("ACTIVE"))
        {
            throw new UserRejectException("User account is not active. Current status: " + accountStatus);
        }

        if(isProfileLocked.equalsIgnoreCase("YES"))
        {
            throw new UserRejectException("Profile Locked!");
        }

        if(isProfileSuspended.equalsIgnoreCase("YES"))
        {
            throw new UserRejectException("Profile Suspended!");
        }
        String sessionId = "session_eY" + UUID.randomUUID() + "_.*";
        Instant currentTime = Instant.now();
        Instant expiryTime = currentTime.plusSeconds(3600);

        UserAuthEntity entity = userAuthEntity.get();
        entity.setLast_login_time(currentTime.toString());
        userAuthRepo.save(entity);

        Map<String, Object> sessionInfo = new LinkedHashMap<>();
        sessionInfo.put("csrf", csrf);
        sessionInfo.put("username", username);
        sessionInfo.put("sessionId", sessionId);
        sessionInfo.put("expiryTime", expiryTime);
        sessionInfo.put("correlationId", correlationId);
        sessionInfo.put("channel", channel);
        sessionInfo.put("cam_level", 30);
        sessionHandler.getSessions().add(sessionInfo);


        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sessionId", sessionId);
        response.put("expiryTime", expiryTime.toString());

        logger.info(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<Map<String, Object>> admin_login_username_password(String csrf, String userId, String password, String correlationId, String channel) throws Exception {

        Optional<UserEntity> emailUser = userRepo.findByUsername(userId);
        if(!emailUser.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email address!");
        }

        Optional<UserEntity> userReal = userRepo.findByUsernameAndPassword(userId, dataEncrypter.encryptData(password));
        String username = emailUser.get().getUsername();
        Optional<UserAuthEntity> userAuthEntity = userAuthRepo.findByUsername(username);
        if(!userAuthEntity.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User authentication details not found!");
        }
        if(!userReal.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password!");
        }

        String accountStatus = userAuthEntity.get().getAccount_status();
        String isProfileLocked = userAuthEntity.get().getIs_account_locked();
        String isProfileSuspended = userAuthEntity.get().getIsProfileSuspended();
        if(!accountStatus.equalsIgnoreCase("ACTIVE"))
        {
            throw new UserRejectException("User account is not active. Current status: " + accountStatus);
        }

        if(isProfileLocked.equalsIgnoreCase("YES"))
        {
            throw new UserRejectException("Profile Locked!");
        }

        if(isProfileSuspended.equalsIgnoreCase("YES"))
        {
            throw new UserRejectException("Profile Suspended!");
        }
        String sessionId = "session_eY" + UUID.randomUUID() + "_.*";
        Instant currentTime = Instant.now();
        Instant expiryTime = currentTime.plusSeconds(3600);

        UserAuthEntity entity = userAuthEntity.get();
        entity.setLast_login_time(currentTime.toString());
        userAuthRepo.save(entity);

        Map<String, Object> sessionInfo = new LinkedHashMap<>();
        sessionInfo.put("csrf", csrf);
        sessionInfo.put("username", username);
        sessionInfo.put("sessionId", sessionId);
        sessionInfo.put("expiryTime", expiryTime);
        sessionInfo.put("correlationId", correlationId);
        sessionInfo.put("channel", channel);
        sessionInfo.put("cam_level", 30);
        sessionHandler.getSessions().add(sessionInfo);

        Map<String, Object> session = policyDecisionPointService.sessionAttributes(sessionId, csrf).getBody();
        String role = session.get("role").toString();

        if(!role.equals("ADMIN"))
        {
            sessionHandler.getSessions().remove(session);
            logger.warn("code=401, reason = unauthorized, message=Only admins can access this site!");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only admins can access this site!");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sessionId", sessionId);
        response.put("expiryTime", expiryTime.toString());

        logger.info(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
