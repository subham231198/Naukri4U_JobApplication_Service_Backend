package com.example.job.portal.Naukri4U.Controller;

import com.example.job.portal.Naukri4U.Entity.CompanyEntity;
import com.example.job.portal.Naukri4U.Service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping(
            value = "/v1/register/company",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<Map<String, Object>> addCompany(@RequestHeader(value = "X-SESSIONID") String tokenId, @RequestHeader(value = "X-XSRF-TOKEN") String csrf, @RequestHeader(value = "channel") String channel, @RequestBody CompanyEntity companyEntity) throws Exception {
        if(csrf==null || csrf.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(tokenId==null || tokenId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-SESSIONID header cannot be null or blank!");
        }
        if(channel==null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "channel header cannot be null or blank!");
        }
        if(!channel.equals("WEB") && !channel.equals("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid channel header value provided!");
        }

        return companyService.addCompany(tokenId, csrf, companyEntity);
    }
}
