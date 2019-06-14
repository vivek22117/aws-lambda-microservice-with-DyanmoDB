package com.vivek.tsr.service;

import com.vivek.tsr.db.DynamoDBOperation;
import com.vivek.tsr.domain.RSVPEventRecord;
import com.vivek.tsr.entity.LatestRSVPRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashSet;

/**
 * Created by Vivek Kumar Mishra on 18-03-2018.
 */
public class DDBPersistenceService {

    private DynamoDBOperation dynamoDBOperation;
    private static final Logger LOGGER = LoggerFactory.getLogger(DDBPersistenceService.class);

    public DDBPersistenceService() {
        this(new DynamoDBOperation());
    }

    private DDBPersistenceService(DynamoDBOperation dynamoDBOperation) {
        this.dynamoDBOperation = dynamoDBOperation;
    }

    public void processRecord(RSVPEventRecord rsvpEventRecord) {
        HashSet<LatestRSVPRecord> reportedRecords = new HashSet<>();
        Instant rsvpTime = Instant.ofEpochMilli(rsvpEventRecord.getMtime());

        reportedRecords.add(createDDBRecord(rsvpEventRecord, rsvpTime));

        reportedRecords.forEach(reportedRecord -> dynamoDBOperation.save(reportedRecord));
    }

    private LatestRSVPRecord createDDBRecord(RSVPEventRecord rsvpEventRecord, Instant rsvpTime) {
        LatestRSVPRecord latestRSVPRecord = new LatestRSVPRecord();
        latestRSVPRecord.setRsvpId(String.valueOf(rsvpEventRecord.getRsvp_id()));
        latestRSVPRecord.setRsvpMakeTime(rsvpTime.toString());
        latestRSVPRecord.setCreatedDate(Instant.now().toString());
        latestRSVPRecord.setRsvpEvent(rsvpEventRecord.getEvent());
        latestRSVPRecord.setRsvpEventRecord(rsvpEventRecord);
        return latestRSVPRecord;
    }
}
