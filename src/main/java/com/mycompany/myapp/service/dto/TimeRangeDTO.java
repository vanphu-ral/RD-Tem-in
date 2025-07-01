package com.mycompany.myapp.service.dto;

public class TimeRangeDTO {
    private String startTime;
    private String endTime;

    public TimeRangeDTO() {
    }

    public TimeRangeDTO(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
