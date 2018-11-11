package com.vivek.tsr.service;

import com.vivek.tsr.domain.EmployeeRecord;
import com.vivek.tsr.entity.LatestEmployeeRecord;
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

    //created key using EmployeeId and CompanyId
    public void processRecords(List<EmployeeRecord> employeeRecords) {
        HashSet<LatestEmployeeRecord> reportedRecords = new HashSet<>();
        Map<String, List<EmployeeRecord>> gpiRecordsWithKeyAndList = employeeRecords.stream()
                .filter(record -> record.getEmpDesignation().contains("Software-Engineer"))
                .collect(Collectors.groupingBy(record -> record.getEmployeeId() + "-" + record.getCompanyId(), toList()));

        gpiRecordsWithKeyAndList.entrySet().stream().forEach(stringListEntry -> {
            if (stringListEntry.getValue().size() > 0) {
                stringListEntry.getValue().stream().forEach(record -> {
                    reportedRecords.add(createDDBRecord(record));
                });
            }
        });

        reportedRecords.stream().forEach(reportedRecord -> dynamoDBOperation.save(reportedRecord));
    }


    private LatestEmployeeRecord createDDBRecord(EmployeeRecord record) {
        LatestEmployeeRecord latestEmployeeRecord = new LatestEmployeeRecord();
        List<String> timeIntervals = new ArrayList<>();
        timeIntervals.add(record.getEventTime());
        latestEmployeeRecord.setEmpIdCompanyId(record.getEmployeeId() + "-" + record.getCompanyId());
        latestEmployeeRecord.setCompanyId(record.getCompanyId());
        latestEmployeeRecord.setEmployeeId(record.getEmployeeId());
        latestEmployeeRecord.setCreatedDate(String.valueOf(Instant.now()));
        latestEmployeeRecord.setTimeIntervals(timeIntervals);
        return latestEmployeeRecord;
    }

    /*//created key of eventTimestamp
    public void processRecordsForDifferentKey() {
        List<EmployeeRecord> gpiRecords = Arrays.asList(createGpiRecords("2018-03-30T20:30:05.000Z",1001L),
                createGpiRecords("2018-03-30T20:05:05.000Z",1002L), createGpiRecords("2018-03-30T18:00:05.000Z",1003L),
                createGpiRecords("2018-03-30T14:25:05.000Z",1006L),createGpiRecords("2018-03-30T13:25:05.000Z",1005L),
                createGpiRecords("2018-03-30T10:25:05.000Z",1006L),createGpiRecords("2018-03-30T11:25:05.000Z",1007L),
                createGpiRecords("2018-03-30T05:25:05.000Z",1006L),createGpiRecords("2018-03-30T05:25:05.000Z",1009L));
        HashSet<LatestEmployeeRecord> reportedRecords = new HashSet<>();
        Map<String, List<EmployeeRecord>> gpiRecordsWithKeyAndList = gpiRecords.stream()
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
                p.getValue().stream().sorted(Comparator.comparing(EmployeeRecord::getEventTime).reversed()).collect(toList()).get(p.getValue().size()-1).getEventTime() +
                        "-" + p.getValue().stream().sorted(Comparator.comparing(EmployeeRecord::getEventTime).reversed()).collect(toList()).get(0).getEventTime()));*//*


//        System.out.println(mapOfKeyAndTime);
        reportedRecords.stream().forEach(reportedRecord -> dynamoDBOperation.save(reportedRecord));
        reportedRecords.stream().forEach(reportedRecord -> dynamoDBOperation.updateItem(reportedRecord));
    }
    private EmployeeRecord createGpiRecords(String eventTime, long deviceId) {
        EmployeeRecord  gpiRecord = new EmployeeRecord();
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
