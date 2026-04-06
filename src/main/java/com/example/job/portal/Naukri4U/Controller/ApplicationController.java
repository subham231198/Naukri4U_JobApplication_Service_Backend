package com.example.job.portal.Naukri4U.Controller;

import com.example.job.portal.Naukri4U.DTO.KeepAlive;
import com.example.job.portal.Naukri4U.Service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping(
            value = "/candidate/job/apply",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> apply_to_job(
            @RequestParam(value = "_jobId") String jobReqId,
            @RequestHeader("X-XSRF-TOKEN") String csrf,
            @RequestHeader("channel") String channel,
            @RequestBody KeepAlive keepAlive)
    {
        if(jobReqId==null || jobReqId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Request param cannot be null or blank!");
        }
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(keepAlive.getTokenId()==null || keepAlive.getTokenId().isEmpty())
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
        return applicationService.apply(keepAlive.getTokenId(), csrf, jobReqId);
    }

    @PostMapping(
            value = "/recruiter/job",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> getApplicants(
            @RequestParam(value = "_jobId") String jobReqId,
            @RequestHeader("X-XSRF-TOKEN") String csrf,
            @RequestHeader("channel") String channel,
            @RequestBody KeepAlive keepAlive
    ) throws Exception {
        if(jobReqId==null || jobReqId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Request param cannot be null or blank!");
        }
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(keepAlive.getTokenId()==null || keepAlive.getTokenId().isEmpty())
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
        return applicationService.getAllCandidatesByJobId(keepAlive.getTokenId(), csrf, jobReqId);
    }
}
