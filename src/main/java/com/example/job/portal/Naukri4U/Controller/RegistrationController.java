package com.example.job.portal.Naukri4U.Controller;

import com.example.job.portal.Naukri4U.Entity.UserEntity;
import com.example.job.portal.Naukri4U.Service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class RegistrationController
{
    @Autowired
    private RegistrationService registrationService;

    @PostMapping(
            value = "/user/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> register(@RequestHeader(name = "X-XSRF-TOKEN") String csrf, @RequestHeader(name = "channel") String channel, @RequestBody UserEntity userEntity) throws Exception {
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(channel== null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Channel header is required");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid header channel");
        }
        return registrationService.register(userEntity);
    }


    @PostMapping(
            value = "/admin/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> register_admin(@RequestHeader(name = "X-XSRF-TOKEN") String csrf, @RequestHeader(name = "channel") String channel, @RequestBody UserEntity userEntity) throws Exception {
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(channel== null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Channel header is required");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid header channel");
        }
        return registrationService.admin_register(userEntity);
    }
}
