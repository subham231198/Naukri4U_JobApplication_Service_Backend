package com.example.job.portal.Naukri4U.Controller;

import com.example.job.portal.Naukri4U.DTO.EmailLoginRequest;
import com.example.job.portal.Naukri4U.DTO.LoginRequest;
import com.example.job.portal.Naukri4U.Service.AdminTestOTPService;
import com.example.job.portal.Naukri4U.Service.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class AuthenticationController
{
    @Autowired
    private AuthenticationService authService;

    @Autowired
    private AdminTestOTPService adminTestOTPService;

    private static final Logger logger = LogManager.getLogger(AuthenticationController.class);

    @PostMapping("/login/v1/auth/email-password")
    private ResponseEntity<Map<String, Object>> login_email_password(@RequestHeader(name = "X-XSRF-TOKEN") String csrf, @RequestHeader(name = "correlationId") String correlationId, @RequestHeader(name = "channel") String channel, @RequestBody EmailLoginRequest loginRequest) throws Exception {
        if(csrf == null || csrf.isEmpty() )
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(correlationId==null || correlationId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "correlationId header cannot be null or blank!");
        }

        if(channel==null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "channel header cannot be null or blank!");
        }

        if(!channel.equals("WEB") && !channel.equals("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid channel header value provided!");
        }

        String email = loginRequest.getCallbackValue_email().getValue();
        String password = loginRequest.getCallbackValue_password().getValue();

        if(email.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be null or blank!");
        }

        if(password.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be null or blank!");
        }

        return authService.login_email_password(csrf, email, password, correlationId, channel);
    }


}
