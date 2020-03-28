package com.vivek.tsr.domain;


public class RSVPResponse {

    private String contentType;
    private String content;


    public RSVPResponse(String content, String contentType) {
        this.contentType = contentType;
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
