package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * A DTO for the check response.
 */
public class CheckResponseDTO implements Serializable {

    @JsonProperty("errCode")
    private Integer errCode;

    @JsonProperty("message")
    private String message;

    public CheckResponseDTO() {}

    public CheckResponseDTO(Integer errCode, String message) {
        this.errCode = errCode;
        this.message = message;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return (
            "CheckResponseDTO{" +
            "errCode=" +
            errCode +
            ", message='" +
            message +
            '\'' +
            '}'
        );
    }
}
