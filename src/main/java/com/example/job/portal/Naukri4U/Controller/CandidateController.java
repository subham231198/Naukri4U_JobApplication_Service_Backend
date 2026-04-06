package com.example.job.portal.Naukri4U.Controller;

import com.example.job.portal.Naukri4U.Entity.CandidateEntity;
import com.example.job.portal.Naukri4U.Service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PostMapping(
            value = "/candidate/session",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> updateCandidateDetails(@RequestParam("_action") String action, @RequestHeader("X-SESSIONID") String tokenId, @RequestHeader("X-XSRF-TOKEN") String csrf, @RequestHeader("channel") String channel, @RequestBody CandidateEntity candidateEntity)
    {
        if(action==null || action.isEmpty() || action.isBlank())
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Query param missing in request");
        }
        if(!action.equals("update"))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(tokenId==null || tokenId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "tokenId cannot be null or blank!");
        }
        if(channel== null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403),"Channel header is required");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Invalid header channel");
        }
        return candidateService.updateCandidateInfo(tokenId, csrf, candidateEntity);
    }
}
