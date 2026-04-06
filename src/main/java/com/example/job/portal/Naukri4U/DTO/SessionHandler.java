package com.example.job.portal.Naukri4U.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class SessionHandler
{
    private List<Map<String, Object>> sessions = new ArrayList<>();

    public List<Map<String, Object>> getSessions() {
        return sessions;
    }

    public void setSessions(List<Map<String, Object>> sessions) {
        this.sessions = sessions;
    }
}

