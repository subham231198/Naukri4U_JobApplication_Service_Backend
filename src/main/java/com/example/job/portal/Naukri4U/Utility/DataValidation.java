package com.example.job.portal.Naukri4U.Utility;

import com.example.job.portal.Naukri4U.Repository.CompanyRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class DataValidation
{

    private static final Logger logger = LogManager.getLogger(DataValidation.class);

    @Autowired
    CompanyRepo companyRepo;

    public boolean isValidEmail(String email, String role)
    {
        logger.info("Validating email: {} for role: {}", email, role);
        if(role.equals("CANDIDATE"))
        {
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            return email != null && email.matches(emailRegex);
        }
        if(role.equals("RECRUITER"))
        {
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (email != null && email.matches(emailRegex)) {
                String[] emailParts = email.split("@", 2);
                String domain = emailParts[1];
                boolean validate_domain = companyRepo.findByDomain(domain) != null;
                if(validate_domain)
                {
                    return true;
                }
                else
                {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company domain not registered for recruiter email!");
                }
            }
        }
        return false;
    }

    public boolean isValidPhoneNumber(String phoneNumber)
    {
        String phoneRegex = "^[0-9]{10}$";
        return phoneNumber != null && phoneNumber.matches(phoneRegex);
    }

    public boolean isValidPassword(String password)
    {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password != null && password.matches(passwordRegex);
    }
}
