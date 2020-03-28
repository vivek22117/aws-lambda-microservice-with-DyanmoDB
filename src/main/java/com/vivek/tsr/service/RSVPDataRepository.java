package com.vivek.tsr.service;

import com.vivek.tsr.db.DynamoDBOperation;
import com.vivek.tsr.domain.RSVPEventRecord;
import com.vivek.tsr.domain.RSVPRequest;

import java.util.ArrayList;
import java.util.List;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;

public class RSVPDataRepository {

    int ddbLimit = 1;

    public List<String> getLastReportedDateFromDDB(RSVPRequest RSVPRequest, DynamoDBOperation dbOperation) {
        if (!isNullOrEmpty(RSVPRequest.getStartTime()) && !isNullOrEmpty(RSVPRequest.getEndTime())) {
            dbOperation.getEventByRSVPAndEventId(RSVPRequest.getRsvp_id(), RSVPRequest.getEvent_id(), ddbLimit);
        }
        return null;
    }

    List<RSVPEventRecord> getHistoricalData(RSVPRequest rsvpRequest) {

        return new ArrayList<>();
    }


    List<RSVPEventRecord> getLatestRecord(RSVPRequest rsvpRequest){

        return new ArrayList<>();
    }
}
