package com.vivek.tsr.service;

import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.entity.LatestReportedRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by Vivek Kumar Mishra on 18-03-2018.
 */
public class DDBPersistenceService {

    private DynamoDBOperation dynamoDBOperation;
    private Logger logger;

    public DDBPersistenceService() {
        this(new DynamoDBOperation(), LogManager.getLogger(DDBPersistenceService.class));
    }

    public DDBPersistenceService(DynamoDBOperation dynamoDBOperation, Logger logger) {
        this.dynamoDBOperation = dynamoDBOperation;
        this.logger = logger;
    }

    //created key using terminalID and orgID
    public void processRecords(List<GpiRecord> gpiRecords) {
        HashSet<LatestReportedRecord> reportedRecords = new HashSet<>();
        Map<String, List<GpiRecord>> gpiRecordsWithKeyAndList = gpiRecords.stream()
                .filter(record -> record.getContentType().contains("tsr-content-schema"))
                .collect(Collectors.groupingBy(record -> record.getDeviceId() + "-" + record.getOrgId(), toList()));

        gpiRecordsWithKeyAndList.entrySet().stream().forEach(stringListEntry -> {
            if (stringListEntry.getValue().size() > 0) {
                stringListEntry.getValue().stream().forEach(record -> {
                    reportedRecords.add(createDDBRecord(record));
                });
            }
        });

        reportedRecords.stream().forEach(reportedRecord -> dynamoDBOperation.save(reportedRecord));
    }


    private LatestReportedRecord createDDBRecord(GpiRecord record) {
        LatestReportedRecord latestReportedRecord = new LatestReportedRecord();
        List<String> timeIntervals = new ArrayList<>();
        timeIntervals.add(record.getEventTime());
        latestReportedRecord.setDeviceIdCompanyId(record.getDeviceId() + "-" + record.getCompanyId());
        latestReportedRecord.setCompanyId(record.getCompanyId());
        latestReportedRecord.setEmployeeId(record.getEmployeeId());
        latestReportedRecord.setCreatedDate(String.valueOf(Instant.now()));
        latestReportedRecord.setTimeIntervals(timeIntervals);
        return latestReportedRecord;
    }

    /*//created key of eventTimestamp
    public void processRecordsForDifferentKey() {
        List<GpiRecord> gpiRecords = Arrays.asList(createGpiRecords("2018-03-30T20:30:05.000Z",1001L),
                createGpiRecords("2018-03-30T20:05:05.000Z",1002L), createGpiRecords("2018-03-30T18:00:05.000Z",1003L),
                createGpiRecords("2018-03-30T14:25:05.000Z",1006L),createGpiRecords("2018-03-30T13:25:05.000Z",1005L),
                createGpiRecords("2018-03-30T10:25:05.000Z",1006L),createGpiRecords("2018-03-30T11:25:05.000Z",1007L),
                createGpiRecords("2018-03-30T05:25:05.000Z",1006L),createGpiRecords("2018-03-30T05:25:05.000Z",1009L));
        HashSet<LatestReportedRecord> reportedRecords = new HashSet<>();
        Map<String, List<GpiRecord>> gpiRecordsWithKeyAndList = gpiRecords.stream()
                .filter(record -> record.getContentType().contains("tsr-content-schema"))
                .collect(Collectors.groupingBy(record -> record.getDeviceId() + "-" + record.getOrgId(), toList()));

        gpiRecordsWithKeyAndList.entrySet().stream().forEach(stringListEntry -> {
            if (stringListEntry.getValue().size() > 0) {
                stringListEntry.getValue().stream().forEach(record -> {
                    reportedRecords.add(createDDBRecord(record));
                });
            }
        });
        System.out.println(reportedRecords);

        *//*Map<String, String> mapOfKeyAndTime = new HashMap<>();

        gpiRecords.stream()
                .filter(record -> record.getContentType().contains("tsr-content-schema"))
                .collect(Collectors.groupingBy(record -> record.getDeviceId() + "-" + record.getOrgId(), toList()))
                .entrySet().stream().forEach(p -> mapOfKeyAndTime.put(p.getKey(),
                p.getValue().stream().sorted(Comparator.comparing(GpiRecord::getEventTime).reversed()).collect(toList()).get(p.getValue().size()-1).getEventTime() +
                        "-" + p.getValue().stream().sorted(Comparator.comparing(GpiRecord::getEventTime).reversed()).collect(toList()).get(0).getEventTime()));*//*


//        System.out.println(mapOfKeyAndTime);
        reportedRecords.stream().forEach(reportedRecord -> dynamoDBOperation.save(reportedRecord));
        reportedRecords.stream().forEach(reportedRecord -> dynamoDBOperation.updateItem(reportedRecord));
    }
    private GpiRecord createGpiRecords(String eventTime, long deviceId) {
        GpiRecord  gpiRecord = new GpiRecord();
        gpiRecord.setDeviceId(deviceId);
        gpiRecord.setOrgId("2001");
        gpiRecord.setCompanyId("MM1");
        gpiRecord.setContentType("tsr-content-schema");
        gpiRecord.setEventTime(eventTime);
        gpiRecord.setEmployeeId("5001");
        gpiRecord.setMachineId(21L);
        return gpiRecord;
    }*/
}
