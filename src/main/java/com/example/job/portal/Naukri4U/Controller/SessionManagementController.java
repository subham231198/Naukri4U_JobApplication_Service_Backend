package com.example.job.portal.Naukri4U.Controller;

import com.example.job.portal.Naukri4U.DTO.KeepAlive;
import com.example.job.portal.Naukri4U.DTO.LogOffProvider;
import com.example.job.portal.Naukri4U.Service.PolicyDecisionPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class SessionManagementController {

    @Autowired
    private PolicyDecisionPointService pdpService;

    @PostMapping(value = "/v1/session/keep-alive",
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<Map<String, Object>> keepAlive(@RequestHeader("X-XSRF-TOKEN") String csrf,
                                                         @RequestHeader(name = "X-SESSIONID") String sessionId,
                                                         @RequestBody KeepAlive request) {
        String tokenId = request.getTokenId();
        if(csrf == null || csrf.isEmpty() )
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(tokenId.isEmpty() || tokenId==null)
        {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be null or empty!");
        }
        if(sessionId==null || sessionId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-SESSIONID header cannot be null or blank!");
        }
        if(!sessionId.equals(tokenId))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }
        return pdpService.keepAlive(tokenId, csrf);
    }


    @PostMapping(value = "/v1/session/info", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> sessionAttributes(
            @RequestHeader("X-XSRF-TOKEN") String csrf,
            @RequestHeader(name = "X-SESSIONID") String sessionId,
            @RequestBody KeepAlive request) {
        String tokenId = request.getTokenId();
        if(csrf == null || csrf.isEmpty() )
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(tokenId.isEmpty() || tokenId==null)
        {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be null or empty!");
        }
        if(sessionId==null || sessionId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-SESSIONID header cannot be null or blank!");
        }
        if(!sessionId.equals(tokenId))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }
        return pdpService.sessionAttributes(tokenId, csrf);
    }

    @PostMapping(value = "/v1/session/logout", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> logout(
            @RequestHeader("X-XSRF-TOKEN") String csrf,
            @RequestBody LogOffProvider logOffProvider)
    {
        String token_type = logOffProvider.getInputTokenState().getToken_type();
        String tokenId = logOffProvider.getInputTokenState().getTokenId();
        if(csrf == null || csrf.isEmpty() )
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-XSRF-TOKEN header cannot be null or blank!");
        }
        if(token_type.isEmpty() || token_type==null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "token_type cannot be null or empty!");
        }
        if(tokenId.isEmpty() || tokenId==null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be null or empty!");
        }
        if(token_type.equals("SSOTOKEN"))
        {
            return pdpService.logoff_provider(tokenId, csrf);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unsupported token type provided!");
        }
    }
}
