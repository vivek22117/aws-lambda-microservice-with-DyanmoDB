package com.vivek.tsr.service;

import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.entity.LatestReportedRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.acl.LastOwnerException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Vivek Kumar Mishra on 18-03-2018.
 */
public class DDBPersistenceService {

    private DynamoDBOperation dynamoDBOperation;
    private org.apache.logging.log4j.Logger logger;

    public DDBPersistenceService() {
        this(new DynamoDBOperation(), LogManager.getLogger(DDBPersistenceService.class));
    }

    public DDBPersistenceService(DynamoDBOperation dynamoDBOperation, Logger logger) {
        this.dynamoDBOperation = dynamoDBOperation;
        this.logger = logger;
    }

    public void processRecords(List<GpiRecord> gpiRecords) {
        HashSet<LatestReportedRecord> reportedRecords = new HashSet<>();
        Map<String, List<GpiRecord>> gpiRecordsWithKeyAndList = gpiRecords.stream().filter(record -> record.getContentType().contains("tsr-content-schema"))
                .collect(Collectors.groupingBy(record -> record.getDeviceId() + "-" + record.getOrgId(), Collectors.toList()));

        gpiRecordsWithKeyAndList.entrySet().stream().forEach(stringListEntry -> {
            if(stringListEntry.getValue().size() > 0){
                stringListEntry.getValue().stream().forEach(record->{
                    reportedRecords.add(createDDBRecord(record));
                });
            }
        });

        reportedRecords.stream().forEach(reportedRecord -> dynamoDBOperation.save(reportedRecord));
    }

    private LatestReportedRecord createDDBRecord(GpiRecord record) {
        LatestReportedRecord latestReportedRecord = new LatestReportedRecord();
        latestReportedRecord.setDeviceIdCompanyId(record.getDeviceId()+"-"+record.getCompanyId());
        latestReportedRecord.setCompanyId(record.getCompanyId());
        latestReportedRecord.setEmployeeId(record.getEmployeeId());
        latestReportedRecord.setCreatedDate(String.valueOf(Instant.now()));
        return latestReportedRecord;
    }


}
