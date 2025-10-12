package com.mycompany.renderQr.domain;

public class GenerateTemResponse {

    private boolean success;
    private String message;
    private int totalTems;

    // CONSTRUCTOR KHÔNG THAM SỐ - BẮT BUỘC!
    public GenerateTemResponse() {}

    public GenerateTemResponse(boolean success, String message, int totalTems) {
        this.success = success;
        this.message = message;
        this.totalTems = totalTems;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getTotalTems() {
        return totalTems;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTotalTems(int totalTems) {
        this.totalTems = totalTems;
    }
}
