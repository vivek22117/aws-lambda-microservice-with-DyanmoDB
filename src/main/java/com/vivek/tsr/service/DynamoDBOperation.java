package com.vivek.tsr.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.vivek.tsr.domain.GpiRecord;

/**
 * Created by HARSHA on 20-02-2018.
 */
public class DynamoDBOperation {

    private final AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1")
                .withCredentials(new ProfileCredentialsProvider("myProfile"));



    private AmazonDynamoDB createClint(){
        return builder.build();
    }

    public boolean getItem(Long deviceId, String orgId, GpiRecord gpiRecord){

        return true;
    }
}
