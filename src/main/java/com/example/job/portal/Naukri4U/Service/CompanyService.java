package com.example.job.portal.Naukri4U.Service;

import com.example.job.portal.Naukri4U.Entity.CompanyEntity;
import com.example.job.portal.Naukri4U.Exceptions.CompanyNotFoundException;
import com.example.job.portal.Naukri4U.Exceptions.SessionInvalidException;
import com.example.job.portal.Naukri4U.Repository.CompanyRepo;
import com.example.job.portal.Naukri4U.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PolicyDecisionPointService pdpService;

    public ResponseEntity<Map<String, Object>> getAllCompanies() {
        List<CompanyEntity> companies = companyRepo.findAll();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total_companies", companies.size());
        response.put("companies", companies);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> getCompanyByName(String name) {
        CompanyEntity company = companyRepo.findByCompanyName(name);
        if (company == null) {
            throw new CompanyNotFoundException("Company not found by name: " + name);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("company_name", company.getCompanyName());
        response.put("domain", company.getDomain());
        response.put("industry", company.getIndustry());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> getCompanyByDomain(String domain) {
        CompanyEntity company = companyRepo.findByDomain(domain);
        if (company == null) {
            throw new CompanyNotFoundException("Company not found by domain: " + domain);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("company_name", company.getCompanyName());
        response.put("domain", company.getDomain());
        response.put("industry", company.getIndustry());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> addCompany(String tokenId, String csrf, CompanyEntity companyEntity) {

        try
        {
            Map<String, Object> sessionAttributes = pdpService.sessionAttributes(tokenId, csrf).getBody();

            userRepo.findByUsername(sessionAttributes.get("username").toString()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found!"));

            String role = (String) sessionAttributes.get("role");
            if (role == null || !role.equals("ADMIN")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
            }

            Integer cam_level = (Integer)sessionAttributes.get("authLevel");
            if(cam_level != 40)
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied! AuthLevel = "+cam_level);
            }

            if (companyEntity.getCompanyName() == null || companyEntity.getCompanyName().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company name is required");
            }
            if (companyEntity.getDomain() == null || companyEntity.getDomain().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company domain is required");
            }
            if (companyEntity.getIndustry() == null || companyEntity.getIndustry().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company industry type is required");
            }

            CompanyEntity existingCompanyByName = companyRepo.findByCompanyName(companyEntity.getCompanyName());
            if (existingCompanyByName != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company already exists");
            }

            CompanyEntity existingCompanyByDomain = companyRepo.findByDomain(companyEntity.getDomain());
            if (existingCompanyByDomain != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company with this domain already exists");
            }
            companyRepo.save(companyEntity);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Company added successfully");
            return ResponseEntity.ok(response);
        }
        catch (SessionInvalidException e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error resolving user from JSON");
        }
    }
}

