package com.vivek.tsr.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.vivek.tsr.domain.Event;
import com.vivek.tsr.domain.RSVPEventRecord;

import java.util.List;

@DynamoDBTable(tableName = "RSVPEventTable")
public class LatestRSVPRecord {

    @DynamoDBHashKey(attributeName = "rsvpId")
    private String rsvpId;

    @DynamoDBAttribute(attributeName = "rsvpMakeTime")
    private String rsvpMakeTime;

    @DynamoDBRangeKey(attributeName = "createdDate")
    private String createdDate;

    @DynamoDBAttribute(attributeName = "rsvpRecord")
    private RSVPEventRecord rsvpEventRecord;

    @DynamoDBAttribute(attributeName = "event")
    private Event rsvpEvent;


    public String getRsvpId() {
        return rsvpId;
    }

    public void setRsvpId(String rsvpId) {
        this.rsvpId = rsvpId;
    }

    public String getRsvpMakeTime() {
        return rsvpMakeTime;
    }

    public void setRsvpMakeTime(String rsvpMakeTime) {
        this.rsvpMakeTime = rsvpMakeTime;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public RSVPEventRecord getRsvpEventRecord() {
        return rsvpEventRecord;
    }

    public void setRsvpEventRecord(RSVPEventRecord rsvpEventRecord) {
        this.rsvpEventRecord = rsvpEventRecord;
    }

    public Event getRsvpEvent() {
        return rsvpEvent;
    }

    public void setRsvpEvent(Event rsvpEvent) {
        this.rsvpEvent = rsvpEvent;
    }
}
