package com.example.job.portal.Naukri4U.Utility;

import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Generator {

    public String generateCandidateID()
    {
        String candidateID = "CAND" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return candidateID;
    }


    public String generateRecruiterID()
    {
        String recruiterID = "RECR" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return recruiterID;
    }

    public String generateAdminID()
    {
        String candidateID = "ADMIN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return candidateID;
    }

    public String generateJobReqID()
    {
        int randomNum = (int)(Math.random() * 900000) + 1000000000;
        String jobReqID = "JOB" + randomNum;
        return jobReqID;
    }

    public String GenerateOTP()
    {
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        return otp;
    }
}
