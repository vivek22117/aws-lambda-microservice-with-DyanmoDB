package com.vivek.tsr.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.entity.LatestReportedRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HARSHA on 20-02-2018.
 */
public class DynamoDBOperation {

    private static final String TABLE_NAME = "MyDyanamoDBTable";
    private static final String DEVICE_ID = "deviceId";
    private static final String DB_INDEX = "TSRIndex";
    private DynamoDBMapper mapper = new DynamoDBMapper(createClient());

    private final AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-east-1")
            .withCredentials(new ProfileCredentialsProvider("myProfile"));

    private AmazonDynamoDB createClient() {
        return builder.build();
    }

    public DynamoDBOperation() {
        this.mapper = new DynamoDBMapper(createClient());
    }

    public GpiRecord getItem(Long deviceId) {

        GetItemRequest itemRequest = new GetItemRequest()
                .withTableName(TABLE_NAME)
                .withKey(getItemKey(deviceId));
        AmazonDynamoDB dynamoDB = createClient();
        GetItemResult item = dynamoDB.getItem(itemRequest);

        if (!(item == null)) {
            extractGpiRecord(item);
        }
        return new GpiRecord();
    }

    private void extractGpiRecord(GetItemResult item) {

    }

    private Map<String, AttributeValue> getItemKey(Long deviceId) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(DEVICE_ID, new AttributeValue().withN(String.valueOf(deviceId)));
        return new HashMap<>();
    }

    public boolean save(LatestReportedRecord recordObject) {
        mapper.save(recordObject);
        return true;
    }

    public List<String> getByTerminalAndOrgId(Long terminalId, String orgId, int i) {

        return getDates(terminalId+"-"+orgId, DB_INDEX,i);
    }

    private List<String> getDates(String globalIndex, String dbIndex, int recordsCount) {




        return new ArrayList<String>();
    }
}
