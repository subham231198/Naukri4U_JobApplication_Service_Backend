package com.example.job.portal.Naukri4U.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController
{
    @GetMapping("/health")
    public Map<String, Object> healthCheck()
    {
        return Map.of(
                "status", "UP",
                "version", "1.0.0",
                "description", "Naukri4U Job Portal Backend is healthy and running."
        );
    }
}
