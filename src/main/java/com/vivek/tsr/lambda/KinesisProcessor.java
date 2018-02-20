package com.vivek.tsr.lambda;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.service.DomainService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by HARSHA on 30-01-2018.
 */
public class KinesisProcessor {

    private ObjectMapper objectMapper;
    private DomainService domainService;

    public KinesisProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void processLastRequest(KinesisEvent kinesisEvent) {

        List<KinesisEvent.KinesisEventRecord> records = kinesisEvent.getRecords();
        List<byte[]> collect = records.stream().map(record -> record.getKinesis().getData().array()).collect(Collectors.toList());

        List<String> stringList = collect.stream().map(e -> e.toString()).collect(Collectors.toList());
        List<GpiRecord> gpiRecords = stringList.stream().map(this::convertToObject).collect(Collectors.toList());

        domainService.processRecords(gpiRecords);

    }

    private GpiRecord convertToObject(String data) {
        try {
         return objectMapper.readValue(data, GpiRecord.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
