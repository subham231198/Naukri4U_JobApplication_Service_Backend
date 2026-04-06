package com.example.job.portal.Naukri4U.Controller;


import com.example.job.portal.Naukri4U.DTO.KeepAlive;
import com.example.job.portal.Naukri4U.DTO.LoginRequest;
import com.example.job.portal.Naukri4U.DTO.PendingRecruiterDTO;
import com.example.job.portal.Naukri4U.Service.AdminService;
import com.example.job.portal.Naukri4U.Service.AdminTestOTPService;
import com.example.job.portal.Naukri4U.Service.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminTestOTPService adminTestOTPService;


    @Autowired
    private AuthenticationService authService;


    private static final Logger logger = LogManager.getLogger(AdminController.class);

    @PostMapping(
            value = "/admin/approveRecruiter/id/{recruiterUsername}",
            produces = "application/json",
            consumes = "application/json"
    )
    private ResponseEntity<Map<String, Object>> approveRecruiter(@RequestHeader("X-XSRF-TOKEN") String csrf, @RequestHeader("channel") String channel, @PathVariable("recruiterUsername") String recruiterUsername, @RequestBody KeepAlive keepAlive) throws Exception {
        if(csrf==null || csrf.isEmpty())
        {
            throw new IllegalArgumentException("X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(keepAlive.getTokenId()==null || keepAlive.getTokenId().isEmpty())
        {
            throw new IllegalArgumentException("tokenId cannot be null or blank!");
        }
        if(channel== null || channel.isEmpty())
        {
            throw new IllegalArgumentException("Channel header is required");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new IllegalArgumentException("Invalid header channel");
        }
        if(recruiterUsername==null || recruiterUsername.isEmpty())
        {
            throw new IllegalArgumentException("Recruiter username cannot be null or blank!");
        }
        return adminService.approveRecruiterById(keepAlive.getTokenId(),csrf, recruiterUsername);
    }


    @PostMapping(
            value = "/admin/pendingApprovals/get/all",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<List<PendingRecruiterDTO>> getPendingApprovals(@RequestHeader("X-XSRF-TOKEN") String csrf, @RequestHeader("channel") String channel, @RequestBody KeepAlive keepAlive) throws Exception {
        if(csrf==null || csrf.isEmpty())
        {
            throw new IllegalArgumentException("X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(keepAlive.getTokenId()==null || keepAlive.getTokenId().isEmpty())
        {
            throw new IllegalArgumentException("tokenId cannot be null or blank!");
        }
        if(channel== null || channel.isEmpty())
        {
            throw new IllegalArgumentException("Channel header is required");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new IllegalArgumentException("Invalid header channel");
        }
        return adminService.getPendingRecruiters(keepAlive.getTokenId(), csrf);
    }

    @PostMapping(
            value = "/admin/dpCloud/getOTP",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> getOTP(@RequestParam(value = "userId") String username, @RequestHeader(value = "X-XSRF-TOKEN") String csrf, @RequestHeader("channel") String channel, @RequestBody KeepAlive keepAlive) throws Exception {
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(username==null || username.equals("") || username.isBlank() || username.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param cannot be null or blank!");
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
        return adminTestOTPService.getOTP_admin(keepAlive.getTokenId(), csrf, username);
    }

    @PostMapping("/login/v1/dpCloud/auth")
    private ResponseEntity<Map<String, Object>> login_email_password(@RequestHeader(name = "X-XSRF-TOKEN") String csrf, @RequestHeader(name = "correlationId") String correlationId, @RequestHeader(name = "channel") String channel, @RequestBody LoginRequest loginRequest) throws Exception {
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

        String username = loginRequest.getCallbackValue_username().getValue();
        String password = loginRequest.getCallbackValue_password().getValue();

        if(username.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be null or blank!");
        }

        if(password.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be null or blank!");
        }

        return authService.admin_login_username_password(csrf, username, password, correlationId, channel);
    }

    @PostMapping(
            value = "/admin/dpCloud/session",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> getUserInfo_admin(@RequestParam(value = "_userId") String username, @RequestHeader(value = "X-XSRF-TOKEN") String csrf, @RequestHeader("channel") String channel, @RequestBody KeepAlive keepAlive) throws Exception {
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(username==null || username.equals("") || username.isBlank() || username.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param cannot be null or blank!");
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
        return adminService.getUserInfo_admin(keepAlive.getTokenId(), csrf, username);
    }
}
