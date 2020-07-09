package com.prj.domain;

public class Param {


    private String cKey;
    private String id;
    private String message;

    public String getcKey() { return cKey; }
    public void setcKey(String cKey) { this.cKey = cKey; }
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
