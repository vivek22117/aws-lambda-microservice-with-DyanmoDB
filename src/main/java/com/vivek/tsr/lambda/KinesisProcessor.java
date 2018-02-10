package com.vivek.tsr.lambda;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.tsr.domain.ApiRecord;
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

        List<ApiRecord> apiRecords = collect.stream().map(this::convertToObject).collect(Collectors.toList());

        domainService.processRecords(apiRecords);

        List<String> strings = collect.stream().map(e -> e.toString()).collect(Collectors.toList());
    }

    private ApiRecord convertToObject(byte[] data) {
        try {
         return objectMapper.readValue(data, ApiRecord.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
