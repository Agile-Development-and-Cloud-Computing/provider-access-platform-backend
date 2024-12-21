package com.fuas.providers_access_platform.dto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonResponse<T> {
    private boolean success;
    private String message;
    private String timestamp;
    private T data;


    // Constructors
    public CommonResponse() {}

    public CommonResponse(boolean success, String message,T data ) {
        this.success = success;
        this.message = message;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.timestamp = LocalDateTime.now().format(formatter);
        this.data = data;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Getters and Setters
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", data=" + data +
                '}';
    }


}

