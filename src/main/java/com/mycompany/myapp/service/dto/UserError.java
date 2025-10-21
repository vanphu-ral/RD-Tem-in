package com.mycompany.myapp.service.dto;

/**
 * DTO for representing user-friendly errors in GraphQL responses.
 */
public class UserError {

    private String code;
    private String message;
    private Integer rowIndex;

    public UserError() {}

    public UserError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public UserError(String code, String message, Integer rowIndex) {
        this.code = code;
        this.message = message;
        this.rowIndex = rowIndex;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }
}
