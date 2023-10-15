package ru.khripunov.socialnetworktt.dto;



public class SimpMessage {

    private String message;

    public SimpMessage() {
    }
    public SimpMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
