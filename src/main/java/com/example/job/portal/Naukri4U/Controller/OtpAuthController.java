package com.example.job.portal.Naukri4U.Controller;

import com.example.job.portal.Naukri4U.DTO.KeepAlive;
import com.example.job.portal.Naukri4U.Service.OTPAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class OtpAuthController {

    @Autowired
    private OTPAuthService otpAuthService;

    private static final Logger logger = LogManager.getLogger(OtpAuthController.class);

    @PostMapping(
            value = "/auth/getOTP",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> getOTP(@RequestHeader(value = "X-XSRF-TOKEN") String csrf, @RequestHeader("channel") String channel, @RequestBody KeepAlive keepAlive) throws Exception {
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(channel==null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "channel header cannot be null or blank!");
        }

        if(!channel.equals("WEB") && !channel.equals("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid channel header value provided!");
        }

        if(keepAlive.getTokenId().isEmpty() || keepAlive.getTokenId() == null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be null or blank!");
        }
        logger.info("Incoming request {}", keepAlive.getTokenId());
        return otpAuthService.getOTP(keepAlive.getTokenId(), csrf);
    }

    @PostMapping(
            value = "/v1/auth/validate",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> validateOTP(@RequestHeader(value = "X-XSRF-TOKEN") String csrf, @RequestHeader("channel") String channel, @RequestParam("_otp") String otp, @RequestBody KeepAlive keepAlive) throws Exception {
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(channel==null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "channel header cannot be null or blank!");
        }

        if(!channel.equals("WEB") && !channel.equals("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid channel header value provided!");
        }

        if(keepAlive.getTokenId().isEmpty() || keepAlive.getTokenId() == null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be null or blank!");
        }
        logger.info("Incoming request session = {}", Map.of("tokenId",keepAlive.getTokenId(),"csrf", csrf, "channel", channel, "OTP", otp));
        return otpAuthService.validateOTP(keepAlive.getTokenId(), csrf, otp);
    }
}
