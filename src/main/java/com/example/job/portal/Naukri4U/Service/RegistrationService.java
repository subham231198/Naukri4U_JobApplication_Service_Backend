package com.example.job.portal.Naukri4U.Service;

import com.example.job.portal.Naukri4U.Entity.*;
import com.example.job.portal.Naukri4U.Exceptions.UserAlreadyExistsException;
import com.example.job.portal.Naukri4U.Repository.*;
import com.example.job.portal.Naukri4U.Utility.DataEncrypter;
import com.example.job.portal.Naukri4U.Utility.DataValidation;
import com.example.job.portal.Naukri4U.Utility.Generator;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.*;

@Service
@Transactional
public class RegistrationService
{

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CandidateRepo candidateRepo;

    @Autowired
    private RecruiterRepo recruiterRepo;

    @Autowired
    private DataValidation dataValidation;

    @Autowired
    private Generator generator;

    @Autowired
    private DataEncrypter  dataEncrypter;

    @Autowired
    private UserAuthRepo userAuthRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private OtpRepo otpRepo;


    @Autowired
    private PendingApprovalRepo pendingAccountApprovalQueueRepo;

    public ResponseEntity<Map<String, Object>> register(UserEntity user) throws Exception {
        if (!dataValidation.isValidEmail(user.getEmail(), user.getRole())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email address provided!");
        }

        if (!dataValidation.isValidPhoneNumber(user.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid phone number provided!");
        }

        if (!dataValidation.isValidPassword(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character!");
        }

        if(user.getFirstName().isEmpty() || user.getFirstName()==null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User firstName cannot be null or empty!");
        }

        if(user.getLastName().isEmpty() || user.getLastName()==null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User lastName cannot be null or empty!");
        }

        if(user.getRole()==null || user.getRole().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User role cannot be null or empty!");
        }

        if(user.getCurrent_location()==null || user.getCurrent_location().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User current location cannot be null or empty!");
        }

        Optional<UserEntity> existingUser = userRepo.findByEmail(dataEncrypter.encryptData(user.getEmail()));
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists.");
        }

        Optional<UserEntity> existingUser_phone = userRepo.findByPhoneNumber(dataEncrypter.encryptData(user.getEmail()));
        if (existingUser_phone.isPresent()) {
            throw new UserAlreadyExistsException("User with phone number " + user.getPhoneNumber() + " already exists.");
        }

        String username = null;
        String encrypted_password = dataEncrypter.encryptData(user.getPassword());

        user.setPassword(encrypted_password);
        user.setEmail(dataEncrypter.encryptData(user.getEmail()));
        user.setPhoneNumber(dataEncrypter.encryptData(user.getPhoneNumber()));

        if(user.getRole().equalsIgnoreCase("RECRUITER"))
        {
            RecruiterEntity recruiterEntity = new RecruiterEntity();
            username = generator.generateRecruiterID();
            while (recruiterRepo.findByUsername(username).isPresent())
            {
                username = generator.generateRecruiterID();
            }
            user.setUsername(username);
            recruiterEntity.setUsername(username);
            recruiterEntity.setDesignation("TBD");
            recruiterEntity.setCompany_location("TBD");
            PendingAccountApprovalQueue pendingAccountApprovalQueue = new PendingAccountApprovalQueue();
            pendingAccountApprovalQueue.setUsername(username);
            pendingAccountApprovalQueue.setRole(user.getRole());
            pendingAccountApprovalQueue.setEmail(user.getEmail());

            String[] emailParts = dataEncrypter.decryptData(user.getEmail()).split("@", 2);
            String hostname = emailParts[1];

            CompanyEntity companyEntity = companyRepo.findByDomain(hostname);
            if(companyEntity == null)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company domain not registered for recruiter email!");
            }
            String companyName = companyEntity.getCompanyName();
            pendingAccountApprovalQueue.setCompanyName(companyName);
            recruiterEntity.setCompany_name(companyName);
            recruiterRepo.save(recruiterEntity);
            pendingAccountApprovalQueueRepo.save(pendingAccountApprovalQueue);
        }

        else if(user.getRole().equalsIgnoreCase("CANDIDATE"))
        {
            CandidateEntity candidateEntity = new CandidateEntity();
            username = generator.generateCandidateID();
            while (candidateRepo.findByUsername(username).isPresent())
            {
                username = generator.generateCandidateID();
            }
            user.setUsername(username);
            candidateEntity.setUsername(username);
            candidateEntity.setDesignation("TBD");
            candidateEntity.setExperience(-1);
            candidateEntity.setOrg_name("TBD");
            candidateEntity.setSalary(-1);
            List<String> skill = new ArrayList<>();
            skill.add("TBD");
            candidateEntity.setSkills(skill);
            candidateEntity.setResume_path(null);
            candidateRepo.save(candidateEntity);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user role provided! Role must be either RECRUITER or CANDIDATE.");
        }

        userRepo.save(user);

        Optional<UserAuthEntity> existingAuth = userAuthRepo.findByUsername(username);
        if(existingAuth.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An authentication profile already exists for the generated username. Please try registering again.");
        }

        UserAuthEntity userAuthEntity = new UserAuthEntity();
        userAuthEntity.setUsername(username);
        userAuthEntity.setAccount_lock_time("11-11-1111T00:00:00Z");
        userAuthEntity.setInvalid_login_attempts(0);
        userAuthEntity.setIsProfileSuspended("false");
        userAuthEntity.setSuspension_reason("NA");
        userAuthEntity.setIs_account_locked("false");
        userAuthEntity.setLast_login_time("11-11-1111T00:00:00Z");
        if(user.getRole().equalsIgnoreCase("RECRUITER"))
        {
            userAuthEntity.setAccount_status("Pending Approval");
        }
        else
        {
            userAuthEntity.setAccount_status("Active");
            OTPEntity otpEntity = new OTPEntity();
            if(otpRepo.existsByEmail(user.getEmail()))
            {
                throw new UserAlreadyExistsException("User already exists by email: "+dataEncrypter.decryptData(user.getEmail()));
            }
            if(otpRepo.existsByPhone(user.getPhoneNumber()))
            {
                throw new UserAlreadyExistsException("User already exists by phone: "+dataEncrypter.decryptData(user.getPhoneNumber()));
            }

            otpEntity.setOTP("111111");
            otpEntity.setUsername(username);
            otpEntity.setEmail(user.getEmail());
            otpEntity.setPhone(user.getPhoneNumber());
            otpEntity.setCreated_timeStamp("1111-01-01T11:11:11.111Z");
            otpEntity.setExpiry_timeStamp("1111-01-01T11:11:11.111Z");
            otpRepo.save(otpEntity);
        }
        userAuthRepo.save(userAuthEntity);
        return new ResponseEntity<>(Map.of("message", "User registered successfully"), HttpStatus.CREATED);
    }




    public ResponseEntity<Map<String, Object>> admin_register(UserEntity user) throws Exception {

        if (!dataValidation.isValidPhoneNumber(user.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid phone number provided!");
        }

        if (!dataValidation.isValidPassword(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character!");
        }

        if (user.getFirstName() == null || user.getFirstName().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User firstName cannot be null or empty!");
        }

        if(user.getLastName().isEmpty() || user.getLastName()==null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User lastName cannot be null or empty!");
        }

        if(user.getRole()==null || user.getRole().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User role cannot be null or empty!");
        }

        if(user.getCurrent_location()==null || user.getCurrent_location().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User current location cannot be null or empty!");
        }

        Optional<UserEntity> existingUser_phone = userRepo.findByPhoneNumber(dataEncrypter.encryptData(user.getPhoneNumber()));
        if (existingUser_phone.isPresent()) {
            throw new UserAlreadyExistsException("Admin with phone number " + user.getPhoneNumber() + " already exists.");
        }

        String username = null;
        String encrypted_password = dataEncrypter.encryptData(user.getPassword());

        user.setPassword(encrypted_password);
        user.setPhoneNumber(dataEncrypter.encryptData(user.getPhoneNumber()));
        AdminEntity adminEntity = new AdminEntity();
        if(user.getRole().equalsIgnoreCase("ADMIN"))
        {
            username = generator.generateAdminID();
            while (recruiterRepo.findByUsername(username).isPresent())
            {
                username = generator.generateAdminID();
            }
            user.setUsername(username);

            String baseEmail = user.getFirstName().toLowerCase() + "." + user.getLastName().toLowerCase();
            String domain = "@naukri4u.com";

            String email = baseEmail + domain;
            String encryptedEmail = dataEncrypter.encryptData(email);

            Optional<UserEntity> existingUser = userRepo.findByEmail(encryptedEmail);

            int counter = 1;

            while (existingUser.isPresent()) {
                email = baseEmail + counter + domain;
                encryptedEmail = dataEncrypter.encryptData(email);
                existingUser = userRepo.findByEmail(encryptedEmail);
                counter++;
            }
            user.setEmail(encryptedEmail);
            adminEntity.setUsername(username);
            adminEntity.setDesignation("Executive");
            adminRepo.save(adminEntity);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user role provided! Role must be Admin.");
        }
        userRepo.save(user);
        Optional<UserAuthEntity> existingAuth = userAuthRepo.findByUsername(username);
        if(existingAuth.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An authentication profile already exists for the generated username. Please try registering again.");
        }

        UserAuthEntity userAuthEntity = new UserAuthEntity();
        userAuthEntity.setUsername(username);
        userAuthEntity.setAccount_lock_time("11-11-1111T00:00:00Z");
        userAuthEntity.setInvalid_login_attempts(0);
        userAuthEntity.setIsProfileSuspended("false");
        userAuthEntity.setSuspension_reason("NA");
        userAuthEntity.setIs_account_locked("false");
        userAuthEntity.setLast_login_time("11-11-1111T00:00:00Z");
        userAuthEntity.setAccount_status("Active");
        userAuthRepo.save(userAuthEntity);

        OTPEntity otpEntity = new OTPEntity();
        if(otpRepo.existsByEmail(user.getEmail()))
        {
            throw new UserAlreadyExistsException("User already exists by email: "+dataEncrypter.decryptData(user.getEmail()));
        }
        if(otpRepo.existsByPhone(user.getPhoneNumber()))
        {
            throw new UserAlreadyExistsException("User already exists by phone: "+dataEncrypter.decryptData(user.getPhoneNumber()));
        }

        otpEntity.setOTP("111111");
        otpEntity.setUsername(username);
        otpEntity.setEmail(user.getEmail());
        otpEntity.setPhone(user.getPhoneNumber());
        otpEntity.setCreated_timeStamp("1111-01-01T11:11:11.111Z");
        otpEntity.setExpiry_timeStamp("1111-01-01T11:11:11.111Z");
        otpRepo.save(otpEntity);

        return new ResponseEntity<>(Map.of("message", "Admin registered successfully! username = "+username), HttpStatus.CREATED);
    }
}


