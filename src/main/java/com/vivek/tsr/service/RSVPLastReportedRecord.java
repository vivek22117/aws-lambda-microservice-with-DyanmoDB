package com.vivek.tsr.service;

import com.vivek.tsr.db.DynamoDBOperation;
import com.vivek.tsr.domain.RSVPRequest;
import com.vivek.tsr.utility.AppUtil;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class RSVPLastReportedRecord extends RSVPDataRepository {

    private static final int DDB_LIMIT = 1;
    private DynamoDBOperation dynamoDBOperation;

    public RSVPLastReportedRecord() {
        this(new DynamoDBOperation());
    }

    public RSVPLastReportedRecord(DynamoDBOperation dynamoDBOperation) {
        this.dynamoDBOperation = dynamoDBOperation;
    }

    public APIResponse getLastKnowRSVPRecord(RSVPRequest rsvpRequest) {

        List<String> reportedDates = getLastReportedDateFromDDB(rsvpRequest, dynamoDBOperation);
        List<String> stringIntervals = reportedDates.stream().map(AppUtil::getFormattedDate)
                .map(AppUtil::getNumberOfIntervals).flatMap(List::stream).collect(toList());


        return null;
    }
}
