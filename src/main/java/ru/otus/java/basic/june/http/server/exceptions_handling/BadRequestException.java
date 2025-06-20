package ru.otus.java.basic.june.http.server.exceptions_handling;

public class BadRequestException extends RuntimeException {
    private String code;
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BadRequestException() {
    }

    public BadRequestException(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
