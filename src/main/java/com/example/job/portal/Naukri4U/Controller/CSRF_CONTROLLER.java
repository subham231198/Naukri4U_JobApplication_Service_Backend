package com.example.job.portal.Naukri4U.Controller;

import com.example.job.portal.Naukri4U.DTO.CSRFHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.*;

@RestController
public class CSRF_CONTROLLER
{
    @Autowired
    private CSRFHandler csrfHandler;

    @GetMapping("/csrf-token")
    public Map<String, Object> getCsrfToken() {
        String csrf = UUID.randomUUID().toString();
        Instant issuedAt = Instant.now();

        List<Map<String, Object>> result = new LinkedList<>();
        Map<String, Object> issuedToken = new LinkedHashMap<>();
        issuedToken.put("csrf", csrf);
        issuedToken.put("issuedAt", issuedAt);
        result.add(issuedToken);
        csrfHandler.setCsrfTokens(result);

        return Map.of("token", csrf);
    }
}
