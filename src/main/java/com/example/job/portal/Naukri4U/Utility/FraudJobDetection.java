package com.example.job.portal.Naukri4U.Utility;

import jakarta.persistence.Column;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class FraudJobDetection {

    public Boolean fraudJobDetection(String jobDescription)
    {
        List<String> alertKeys = new ArrayList<>();
        alertKeys.add("Pay registration fee");
        alertKeys.add("fee");
        alertKeys.add("deposit");
        alertKeys.add("Security deposit required");
        alertKeys.add("Training fee");
        alertKeys.add("Earn ₹50,000/day without experience");
        alertKeys.add("Investment required");
        alertKeys.add("Processing fee");
        alertKeys.add("Guaranteed job");
        alertKeys.add("No interview required");
        alertKeys.add("Instant hiring");
        alertKeys.add("Work 2 hours earn ₹1 lakh");
        alertKeys.add("Contact on WhatsApp only");
        alertKeys.add("Whatsapp");
        alertKeys.add("No official email");
        alertKeys.add("International company hiring urgently");
        alertKeys.add("Confidential company");
        alertKeys.add("Send Aadhaar/PAN upfront");
        alertKeys.add("Share bank details for salary processing");
        alertKeys.add("Upload documents before interview");
        alertKeys.add("Copy-paste work from home");
        alertKeys.add("Captcha entry job");
        alertKeys.add("Simple typing job");
        alertKeys.add("Apply immediately or lose opportunity");
        alertKeys.add("Only today hiring");

        for(String e : alertKeys)
        {
            if(jobDescription.toLowerCase().contains(e.toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    public Boolean validateJobDescription(String jobDescription, List<String> requiredSkills)
    {
        String smallCase_description = jobDescription.toLowerCase();

        int length = requiredSkills.size();
        if(length==0)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required skill set for given job cannot be empty!");
        }
        int counter = 0;
        for(String e: requiredSkills)
        {
            if(smallCase_description.contains(e.toLowerCase()))
            {
                counter++;
            }
        }

        int requiredMatch = (int) Math.ceil(length * 0.5);
        if(counter >= requiredMatch)
        {
            return true;
        }
        return false;
    }
}
