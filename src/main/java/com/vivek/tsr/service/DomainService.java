package com.vivek.tsr.service;

import com.vivek.tsr.domain.EmployeeRecord;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.springframework.util.ObjectUtils.*;

/**
 * Created by HARSHA on 05-02-2018.
 */
public class DomainService {

    private DynamoDBOperation dynamoDBOperation;
    private Logger logger;

    public DomainService() {
        this(new DynamoDBOperation(), getLogger(DomainService.class));
    }

    public DomainService(DynamoDBOperation dynamoDBOperation, Logger logger) {
        this.dynamoDBOperation = dynamoDBOperation;
        this.logger = logger;
    }

    public void processRecords(List<EmployeeRecord> employeeRecords) {

        Map<String, List<EmployeeRecord>> mapOfGpiRecords = employeeRecords.parallelStream()
                .filter(record -> record.getEmpDesignation().contains("Software-Engineer"))
                .collect(Collectors.groupingBy(record -> {
                    String deviceId = record.getEmployeeId();
                    String orgId = record.getCompanyId();
                    return deviceId + "-" + orgId;
                }, toList()));

        Map<String, EmployeeRecord> gpiRecordMap = mapOfGpiRecords.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, stringListEntry ->
                        stringListEntry.getValue().stream()
                                .sorted(Comparator.comparing(gpiRecord -> gpiRecord.getEventTime()))
                                .collect(toList()).get(0)));

        persistDataInDynamoDB(gpiRecordMap);

    }

    private void persistDataInDynamoDB(Map<String, EmployeeRecord> gpiRecordMap) {

        gpiRecordMap.entrySet().stream().forEach(stringGpiRecordEntry -> {
            String key = stringGpiRecordEntry.getKey();
            String[] split = key.split("\\.");
            String deviceId = split[0];
            String orgId = split[1];
            EmployeeRecord employeeRecord = stringGpiRecordEntry.getValue();
            boolean recordStatus = isGpiRecordLastest(deviceId, orgId, employeeRecord);
            if(recordStatus){
                dynamoDBOperation.save(employeeRecord);
            }
        });
    }

    private boolean isGpiRecordLastest(String deviceId, String orgId, EmployeeRecord employeeRecord) {
        DynamoDBOperation dynamoDBOperation = new DynamoDBOperation();
        EmployeeRecord item = dynamoDBOperation.getItem(Long.valueOf(deviceId));

        if(!isEmpty(item)){
            Instant dbItemTime = Instant.parse(item.getEventTime());
            Instant gpiRecordTime = Instant.parse(employeeRecord.getEventTime());
            return gpiRecordTime.isAfter(dbItemTime);
        }
        return true;
    }
}
