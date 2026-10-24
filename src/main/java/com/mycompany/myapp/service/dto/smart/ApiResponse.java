package com.mycompany.myapp.service.dto.smart;

import java.util.List;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public static class NoticeData {

        private String noticeNo;
        private int received;
        private List<RejectedLine> rejected;

        public NoticeData(
            String noticeNo,
            int received,
            List<RejectedLine> rejected
        ) {
            this.noticeNo = noticeNo;
            this.received = received;
            this.rejected = rejected;
        }

        public String getNoticeNo() {
            return noticeNo;
        }

        public int getReceived() {
            return received;
        }

        public List<RejectedLine> getRejected() {
            return rejected;
        }
    }

    public static class RejectedLine {

        private String lineId;
        private String reason;

        public RejectedLine(String lineId, String reason) {
            this.lineId = lineId;
            this.reason = reason;
        }

        public String getLineId() {
            return lineId;
        }

        public String getReason() {
            return reason;
        }
    }
}
