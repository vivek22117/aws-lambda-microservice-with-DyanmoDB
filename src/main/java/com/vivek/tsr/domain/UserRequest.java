package com.vivek.tsr.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class UserRequest {
    private Integer count;
    private String startTime;
    private String endTime;
    private String contentType;
    private Integer startIndex;
    private Integer rsvp_id;
    private Long timeStamp;
    private boolean isLastReporting;

    public Integer getRsvp_id() {
        return rsvp_id;
    }

    public void setRsvp_id(Integer rsvp_id) {
        this.rsvp_id = rsvp_id;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isLastReporting() {
        return isLastReporting;
    }

    public void setLastReporting(boolean lastReporting) {
        isLastReporting = lastReporting;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public void initialize() {
        if (isLastReporting) {
            count = 1;
        }
        if (count == 0) {
            count = 50;
        }
        if (startIndex == null) {
            startIndex = 0;
        }
        if (endTime == null) {
            Instant.now().toString();
        }
        if (startTime == null) {
            startTime = Instant.parse(endTime).minus(100, ChronoUnit.MINUTES).toString();
        }

    }
}
