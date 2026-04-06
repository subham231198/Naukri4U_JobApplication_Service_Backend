package com.example.job.portal.Naukri4U.Service;

import com.example.job.portal.Naukri4U.DTO.SessionHandler;
import com.example.job.portal.Naukri4U.Entity.UserEntity;
import com.example.job.portal.Naukri4U.Exceptions.LogoutException;
import com.example.job.portal.Naukri4U.Exceptions.SessionInvalidException;
import com.example.job.portal.Naukri4U.Repository.UserRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PolicyDecisionPointService
{
    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private UserRepo userRepo;

    private static final Logger logger = LogManager.getLogger(PolicyDecisionPointService.class);

    public ResponseEntity<Map<String, Object>> keepAlive(String tokenId, String csrf) {
        Map<String, Object> sessionInfo = sessionHandler.getSessions().stream()
                .filter(session -> session.get("sessionId").equals(tokenId) && session.get("csrf").equals(csrf))
                .findFirst()
                .orElseThrow(() -> new SessionInvalidException("Session "+tokenId+" is invalid or has expired"));

        Instant expiryTime = (Instant) sessionInfo.get("expiryTime");
        if (Instant.now().isAfter(expiryTime)) {
            sessionHandler.getSessions().remove(sessionInfo);
            return new ResponseEntity<>(Map.of("valid", false), HttpStatus.OK);
        }

        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("isSessionValid", true);
        response.put("correlationId", sessionInfo.get("correlationId").toString());
        logger.info("Response {}", response);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> sessionAttributes(String tokenId, String csrf) {
        Map<String, Object> sessionInfo = sessionHandler.getSessions().stream()
                .filter(session -> session.get("sessionId").equals(tokenId) && session.get("csrf").equals(csrf))
                .findFirst()
                .orElseThrow(() -> new SessionInvalidException("Session "+tokenId+" is invalid or has expired"));

        Instant expiryTime = (Instant) sessionInfo.get("expiryTime");
        if (Instant.now().isAfter(expiryTime)) {
            sessionHandler.getSessions().remove(sessionInfo);
            return new ResponseEntity<>(Map.of("valid", false), HttpStatus.OK);
        }

        Optional<UserEntity> entity = userRepo.findByUsername(sessionInfo.get("username").toString());
        if(!entity.isPresent())
        {
            return new ResponseEntity<>(Map.of("valid", false), HttpStatus.OK);
        }

        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("isSessionValid", true);
        response.put("username", sessionInfo.get("username"));
        response.put("correlationId", sessionInfo.get("correlationId"));
        response.put("channel", sessionInfo.get("channel"));
        response.put("firstName", entity.get().getFirstName());
        response.put("lastName", entity.get().getLastName());
        response.put("role", entity.get().getRole());
        response.put("authLevel", sessionInfo.get("cam_level"));

        logger.info("Response {}", response);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> logoff_provider(String tokenId, String csrf) {
        Map<String, Object> sessionInfo = sessionHandler.getSessions().stream()
                .filter(session -> session.get("sessionId").equals(tokenId) && session.get("csrf").equals(csrf))
                .findFirst()
                .orElseThrow(() -> new SessionInvalidException("Session "+tokenId+" is invalid or has expired"));

        Instant expiryTime = (Instant) sessionInfo.get("expiryTime");
        if (Instant.now().isAfter(expiryTime)) {
            sessionHandler.getSessions().remove(sessionInfo);
            throw new LogoutException("Session "+tokenId+" is invalid or has expired");
        }

        sessionHandler.getSessions().remove(sessionInfo);
        logger.info("Response {}", "Successfully logged out of all sessions "+tokenId);
        return new ResponseEntity<>(Map.of("message", "Successfully logged out of all sessions"), HttpStatus.OK);
    }

}
