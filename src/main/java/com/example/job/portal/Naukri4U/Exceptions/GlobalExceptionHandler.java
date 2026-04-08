package com.example.job.portal.Naukri4U.Exceptions;


import com.example.job.portal.Naukri4U.Entity.APIResponse;
import com.example.job.portal.Naukri4U.Repository.UserRepo;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        APIResponse<Void> response = new APIResponse<>();
        response.setCode(400);
        response.setReason("Bad Request");
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleGeneric(Exception ex) {
        APIResponse<Void> response = new APIResponse<>();
        response.setCode(500);
        response.setReason("Internal Server Error");
        response.setMessage("Something went wrong: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<APIResponse<Void>> handleResponseStatus(ResponseStatusException ex) {
        APIResponse<Void> response = new APIResponse<>();
        response.setCode(ex.getStatusCode().value());
        if (ex.getStatusCode().value() == 400) {
            response.setReason("Bad Request");
        }
        if (ex.getStatusCode().value() == 401) {
            response.setReason("Unauthorized");
        }
        if (ex.getStatusCode().value() == 403) {
            response.setReason("Forbidden");
        }
        if (ex.getStatusCode().value() == 404) {
            response.setReason("Not Found");
        }
        if (ex.getStatusCode().value() == 500) {
            response.setReason("Internal Server Error");
        }
        response.setMessage(ex.getReason() != null ? ex.getReason() : "Unexpected error");
        return new ResponseEntity<>(response, ex.getStatusCode());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(error -> ((FieldError) error).getField() + " " + error.getDefaultMessage())
                .orElse("Invalid input");
        APIResponse<Void> response = new APIResponse<>();
        response.setCode(400);
        response.setReason("Bad Request");
        response.setMessage(errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof UnrecognizedPropertyException unrecognized) {

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("code", 400);
            result.put("reason", "Bad Request");
            result.put("message", "Incorrect attribute name: " + unrecognized.getPropertyName());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(result);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 400);
        result.put("reason", "Bad Request");
        result.put("message", "Error in parsing from JSON");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(result);
    }


    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<Map<String, Object>> JobNotFoundException() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 404);
        result.put("reason", "Not Found");
        result.put("message", "Job not found!");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(result);
    }


    @ExceptionHandler(CandidateNotFoundException.class)
    public ResponseEntity<Map<String, Object>> CustomerNotFoundError(){
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 404);
        result.put("reason", "Not Found");
        result.put("message", "Customer not found!");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(result);
    }

    @ExceptionHandler(RecruiterNotFoundException.class)
    public ResponseEntity<Map<String, Object>> RecruiterNotFoundException(){
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 404);
        result.put("reason", "Not Found");
        result.put("message", "Recruiter not found!");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(result);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> UserAlreadyExists() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "User already exists!");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<Map<String, Object>> CompanyNotFound() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "Company not registered with Naukri4U");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }


    @ExceptionHandler(SessionInvalidException.class)
    public ResponseEntity<Map<String, Object>> SessionInvalidException() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("valid", false);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }


    @ExceptionHandler(LogoutException.class)
    public ResponseEntity<Map<String, Object>> LogoutException() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "Error resolving user from JSON");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }

    @ExceptionHandler(UserRejectException.class)
    public ResponseEntity<Map<String, Object>> UserRejectException() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "Login Failure! Pending approval...");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }


    @ExceptionHandler(QuarrentineJobInfoException.class)
    public ResponseEntity<Map<String, Object>> QuarrentinedJob() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "Job is quarantined!");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }

    @ExceptionHandler(InsufficientJobInfoException.class)
    public ResponseEntity<Map<String, Object>> InsufficientJobInfoException() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "Required skills does not match job description! 50% match criteria");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }

    @ExceptionHandler(ApplicationAlreadyExisits.class)
    public ResponseEntity<Map<String, Object>> ApplicationAlreadyExisits() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "Application already exists!");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }

    @ExceptionHandler(NoCandidateAgainstApplicationException.class)
    public ResponseEntity<Map<String, Object>> NoCandidateFound() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "No applicants yet!");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, Object>> AuthFailedException() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "Login Failure!");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }

    @ExceptionHandler(ProfileLockedException.class)
    public ResponseEntity<Map<String, Object>> ProfileLockedException() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "Profile Locked!");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }

    @ExceptionHandler(CareerDetailsNotUpdatedException.class)
    public ResponseEntity<Map<String, Object>> ExpInfoNotUpdatedException() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "Access Denied!");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }

}
