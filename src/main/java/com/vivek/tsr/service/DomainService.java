package com.vivek.tsr.service;

import com.vivek.tsr.domain.GpiRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public void processRecords(List<GpiRecord> gpiRecords) {

        Map<String, List<GpiRecord>> mapOfGpiRecords = gpiRecords.parallelStream()
                .filter(gpiRecord -> gpiRecord.getContentType().contains("tsr-content-schema"))
                .collect(Collectors.groupingBy(gpiRecord -> {
                    Long deviceId = gpiRecord.getDeviceId();
                    String orgId = gpiRecord.getOrgId();
                    return new String(deviceId + "." + orgId);
                }, toList()));

        Map<String, GpiRecord> gpiRecordMap = mapOfGpiRecords.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, stringListEntry ->
                        stringListEntry.getValue().stream()
                                .sorted(Comparator.comparing(gpiRecord -> gpiRecord.getEventTime()))
                                .collect(toList()).get(0)));

        persistDataInDynamoDB(gpiRecordMap);

    }

    private void persistDataInDynamoDB(Map<String, GpiRecord> gpiRecordMap) {

        gpiRecordMap.entrySet().stream().forEach(stringGpiRecordEntry -> {
            String key = stringGpiRecordEntry.getKey();
            String[] split = key.split("\\.");
            String deviceId = split[0];
            String orgId = split[1];
            GpiRecord gpiRecord = stringGpiRecordEntry.getValue();
            boolean recordStatus = isGpiRecordLastest(deviceId, orgId, gpiRecord);
            if(recordStatus){
                dynamoDBOperation.save(gpiRecord);
            }
        });
    }

    private boolean isGpiRecordLastest(String deviceId, String orgId, GpiRecord gpiRecord) {
        DynamoDBOperation dynamoDBOperation = new DynamoDBOperation();
        GpiRecord item = dynamoDBOperation.getItem(Long.valueOf(deviceId));

        if(!isEmpty(item)){
            Instant dbItemTime = Instant.parse(item.getEventTime());
            Instant gpiRecordTime = Instant.parse(gpiRecord.getEventTime());
            return gpiRecordTime.isAfter(dbItemTime);
        }
        return true;
    }
}
