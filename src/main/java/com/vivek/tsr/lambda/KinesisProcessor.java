package com.vivek.tsr.lambda;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.vivek.tsr.domain.EmployeeRecord;
import com.vivek.tsr.service.DDBPersistenceService;
import com.vivek.tsr.service.DomainService;
import com.vivek.tsr.utility.JsonUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by HARSHA on 30-01-2018.
 */
public class KinesisProcessor {

    private Logger logger = LogManager.getLogger(KinesisProcessor.class);
    private JsonUtility jsonUtility;
    private DomainService domainService;
    private DDBPersistenceService ddbPersistenceService;

    public KinesisProcessor() {
        this(new JsonUtility(), new DomainService(),new DDBPersistenceService());
    }

    public KinesisProcessor(JsonUtility jsonUtility, DomainService domainService, DDBPersistenceService ddbPersistenceService) {
        this.jsonUtility = jsonUtility;
        this.domainService = domainService;
        this.ddbPersistenceService = ddbPersistenceService;
    }

    public void processLastRequest(KinesisEvent kinesisEvent) {

        List<KinesisEvent.KinesisEventRecord> records = kinesisEvent.getRecords();
        List<byte[]> collect = records.stream().map(record -> record.getKinesis().getData().array()).collect(Collectors.toList());

        List<String> stringList = collect.stream().map(Object::toString).collect(Collectors.toList());
        List<EmployeeRecord> employeeRecords = stringList.stream().map(this::convertToObject).collect(Collectors.toList());

//        domainService.processRecords(employeeRecords);
        ddbPersistenceService.processRecords(employeeRecords);

    }

    private EmployeeRecord convertToObject(String data) {
        try {
            return jsonUtility.convertFromJson(data,EmployeeRecord.class);
        } catch (IOException e) {
            logger.error("Unable to process string to fetch GPI Record :", e);
        }
        return null;
    }
}
