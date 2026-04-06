package com.example.job.portal.Naukri4U.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailLoginRequest {
    @JsonProperty(value = "callbackInput_email")
    private CallbackInput callbackInput_email;

    @JsonProperty(value = "callbackValue_email")
    private CallbackValue callbackValue_email;

    @JsonProperty(value = "callbackInput_password")
    private CallbackInput callbackInput_password;

    @JsonProperty(value = "callbackValue_password")
    private CallbackValue callbackValue_password;

    public CallbackInput getCallbackInput_email() {
        return callbackInput_email;
    }

    public void setCallbackInput_email(CallbackInput callbackInput_email) {
        this.callbackInput_email = callbackInput_email;
    }

    public CallbackValue getCallbackValue_email() {
        return callbackValue_email;
    }

    public void setCallbackValue_email(CallbackValue callbackValue_email) {
        this.callbackValue_email = callbackValue_email;
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
