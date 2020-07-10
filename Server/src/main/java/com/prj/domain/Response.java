package com.prj.domain;

import java.util.Map;

public class Response {
    private Map<String,String>[] encryptedElements;
    private String sKey;
    private String id;
    private String message;

    public Map<String, String>[] getEncryptedElements() {
        return encryptedElements;
    }

    public void setEncryptedElements(Map<String, String>[] encryptedElements) {
        this.encryptedElements = encryptedElements;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getsKey() {
        return sKey;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }
}
