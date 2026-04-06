package com.example.job.portal.Naukri4U.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class CSRFHandler
{
    List<Map<String, Object>> csrfTokens = new LinkedList<>();

    public List<Map<String, Object>> getCsrfTokens() {
        return csrfTokens;
    }

    public void setCsrfTokens(List<Map<String, Object>> csrfTokens) {
        this.csrfTokens = csrfTokens;
    }


}
