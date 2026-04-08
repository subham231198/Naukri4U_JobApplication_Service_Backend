package com.example.job.portal.Naukri4U.Service;

import com.example.job.portal.Naukri4U.Entity.OTPEntity;
import com.example.job.portal.Naukri4U.Repository.OtpRepo;
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
public class AdminTestOTPService {

    @Autowired
    private OtpRepo otpRepo;

    @Autowired
    private PolicyDecisionPointService pdpService;

    @Autowired
    private Generator generator;

    @Autowired
    private DataEncrypter dataEncrypter;

    private static final Logger logger = LogManager.getLogger(AdminTestOTPService.class);

    public ResponseEntity<Map<String, Object>> getOTP_admin(String tokenId, String csrf, String username) throws Exception {
        Map<String, Object> sessionInfo = pdpService.sessionAttributes(tokenId, csrf).getBody();

        String role = sessionInfo.get("role").toString();
        Map<String, Object> response = new LinkedHashMap<>();

        if(!role.equals("ADMIN"))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied!");
        }
        Optional<OTPEntity> getUser = otpRepo.findByUsername(username);
        if(!getUser.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username provided!");
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
}
