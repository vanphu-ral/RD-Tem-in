package com.mycompany.myapp.service.dto;

import java.util.List;

public class DeliveryNoticeResponse {

    private boolean success;
    private String message;
    private ResponseData data;

    public DeliveryNoticeResponse() {}

    public DeliveryNoticeResponse(
        boolean success,
        String message,
        ResponseData data
    ) {
        this.success = success;
        this.message = message;
        this.data = data;
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

    public ResponseData getData() {
        return data;
    }

    public void setData(ResponseData data) {
        this.data = data;
    }

    public static class ResponseData {

        private String noticeNo;
        private int received;
        private List<RejectedLineDto> rejected;

        public ResponseData() {}

        public ResponseData(
            String noticeNo,
            int received,
            List<RejectedLineDto> rejected
        ) {
            this.noticeNo = noticeNo;
            this.received = received;
            this.rejected = rejected;
        }

        public String getNoticeNo() {
            return noticeNo;
        }

        public void setNoticeNo(String noticeNo) {
            this.noticeNo = noticeNo;
        }

        public int getReceived() {
            return received;
        }

        public void setReceived(int received) {
            this.received = received;
        }

        public List<RejectedLineDto> getRejected() {
            return rejected;
        }

        public void setRejected(List<RejectedLineDto> rejected) {
            this.rejected = rejected;
        }
    }

    public static class RejectedLineDto {

        private String lineId;
        private String reason;

        public RejectedLineDto() {}

        public RejectedLineDto(String lineId, String reason) {
            this.lineId = lineId;
            this.reason = reason;
        }

        public String getLineId() {
            return lineId;
        }

        public void setLineId(String lineId) {
            this.lineId = lineId;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
