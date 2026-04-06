package com.example.job.portal.Naukri4U.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest
{
    @JsonProperty(value = "callbackInput_username")
    private CallbackInput callbackInput_username;

    @JsonProperty(value = "callbackValue_username")
    private CallbackValue callbackValue_username;

    @JsonProperty(value = "callbackInput_password")
    private CallbackInput callbackInput_password;

    @JsonProperty(value = "callbackValue_password")
    private CallbackValue callbackValue_password;

    public CallbackInput getCallbackInput_username() {
        return callbackInput_username;
    }

    public void setCallbackInput_username(CallbackInput callbackInput_username) {
        this.callbackInput_username = callbackInput_username;
    }

    public CallbackValue getCallbackValue_username() {
        return callbackValue_username;
    }

    public void setCallbackValue_username(CallbackValue callbackValue_username) {
        this.callbackValue_username = callbackValue_username;
    }

    public CallbackInput getCallbackInput_password() {
        return callbackInput_password;
    }

    public void setCallbackInput_password(CallbackInput callbackInput_password) {
        this.callbackInput_password = callbackInput_password;
    }

    public CallbackValue getCallbackValue_password() {
        return callbackValue_password;
    }

    public void setCallbackValue_password(CallbackValue callbackValue_password) {
        this.callbackValue_password = callbackValue_password;
    }
}
