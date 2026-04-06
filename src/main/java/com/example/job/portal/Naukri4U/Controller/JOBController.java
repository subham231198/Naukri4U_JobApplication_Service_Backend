package com.example.job.portal.Naukri4U.Controller;

import com.example.job.portal.Naukri4U.DTO.KeepAlive;
import com.example.job.portal.Naukri4U.Entity.JobEntity;
import com.example.job.portal.Naukri4U.Service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class JOBController {

    @Autowired
    private JobService jobService;

    @PostMapping(
            value = "/v1/job/add",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> addJob(@RequestHeader(value = "X-SESSIONID") String tokenId, @RequestHeader(value = "X-XSRF-TOKEN")  String csrf, @RequestHeader(value = "channel") String channel, @RequestBody JobEntity jobEntity) throws Exception {
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(tokenId==null || tokenId.isEmpty())
        {
        throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-SESSIONID header cannot be null or blank!");
        }
        if(channel== null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Channel header is required");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Invalid header channel");
        }
        return jobService.addJob(tokenId, csrf, jobEntity);
    }


    @PostMapping(
            value = "/v1/job/info",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> getJobByCriteria(
            @RequestParam(value = "type") String type,
            @RequestParam("value") String value,
            @RequestHeader(value = "X-XSRF-TOKEN") String csrf,
            @RequestHeader(value = "channel") String channel,
            @RequestBody KeepAlive keepAlive)
    {
        String tokenId = keepAlive.getTokenId();
        if(value==null || value.isEmpty() || value.equals("") || type == null || type.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), "Query param cannot be null or blank!");
        }
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(tokenId==null || tokenId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-SESSIONID header cannot be null or blank!");
        }
        if(channel== null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Channel header is required");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Invalid header channel");
        }

        if(type.equals("id"))
        {
            return jobService.getJobById(tokenId, csrf, value);
        }
        if(type.equals("company"))
        {
            return jobService.getJobsByCompanyName(tokenId, csrf, value);
        }
        if(type.equals("location"))
        {
            return jobService.getJobsByLocation(tokenId, csrf, value);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AccessDenied!");
        }
    }

    @PostMapping(
            value = "/v1/job/info/all",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> getAllJobs(@RequestHeader(value = "X-XSRF-TOKEN") String csrf, @RequestHeader(value = "channel") String channel, @RequestBody KeepAlive keepAlive)
    {
        String tokenId = keepAlive.getTokenId();
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(tokenId==null || tokenId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-SESSIONID header cannot be null or blank!");
        }
        if(channel== null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Channel header is required");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Invalid header channel");
        }

        return jobService.getAllJobs(tokenId, csrf);
    }

    @PostMapping(
            value = "/v1/job/info/criteria1",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> getJobByCriteria1(@RequestParam(value = "min_exp") Integer minExp, @RequestParam(value = "max_exp") Integer maxExp, @RequestHeader(value = "X-XSRF-TOKEN") String csrf, @RequestHeader(value = "channel") String channel, @RequestBody KeepAlive keepAlive)
    {
        String tokenId = keepAlive.getTokenId();
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(tokenId==null || tokenId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-SESSIONID header cannot be null or blank!");
        }
        if(channel== null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Channel header is required");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Invalid header channel");
        }
        if(minExp == null || maxExp==null || maxExp==0)
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Query params cannot be empty or null!");
        }

        return jobService.getJobsByExperienceRange(tokenId, csrf, minExp, maxExp);
    }

    @PostMapping(
            value = "/v1/job/info/criteria2",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> getJobByCriteria2(@RequestHeader(value = "X-XSRF-TOKEN") String csrf, @RequestParam(value = "min_salary") Integer minSalary, @RequestParam(value = "max_salary") Integer maxSalary, @RequestHeader(value = "channel") String channel, @RequestBody KeepAlive keepAlive)
    {
        String tokenId = keepAlive.getTokenId();
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(tokenId==null || tokenId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "X-SESSIONID header cannot be null or blank!");
        }
        if(channel== null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Channel header is required");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Invalid header channel");
        }
        if(minSalary == null || maxSalary==0 || maxSalary==null || maxSalary==0)
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Query params cannot be empty or null!");
        }

        return jobService.getJobsBySalaryRange(tokenId, csrf, minSalary, maxSalary);
    }

}
