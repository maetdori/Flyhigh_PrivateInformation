package com.prj.dto;

public class Param {


    private String id;
    private String message;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    @Override
    public String toString() {
        return "ID : " + id +"\n" +
                "Message : " + message;
    }

}
