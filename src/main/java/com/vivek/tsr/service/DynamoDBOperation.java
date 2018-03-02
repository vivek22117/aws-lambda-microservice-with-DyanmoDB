package com.vivek.tsr.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.vivek.tsr.domain.GpiRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HARSHA on 20-02-2018.
 */
public class DynamoDBOperation {

    private static final String TABLE_NAME = "dyanamoDBTable";
    private static final String DEVICE_ID = "deviceId";

    private final AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-east-1")
            .withCredentials(new ProfileCredentialsProvider("myProfile"));

    private AmazonDynamoDB createClient() {
        return builder.build();
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

    public void save(GpiRecord deviceId) {
    }
}
