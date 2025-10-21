package com.mycompany.myapp.service.dto;

public class GenerateTemResponse {

    private boolean success;
    private String message;
    private int totalTems;

    public GenerateTemResponse() {}

    public GenerateTemResponse(boolean success, String message, int totalTems) {
        this.success = success;
        this.message = message;
        this.totalTems = totalTems;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalTems() {
        return totalTems;
    }

    public void setTotalTems(int totalTems) {
        this.totalTems = totalTems;
    }
}
