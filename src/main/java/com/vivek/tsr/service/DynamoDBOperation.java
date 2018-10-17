package com.vivek.tsr.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.vivek.tsr.domain.EmployeeRecord;
import com.vivek.tsr.entity.LatestEmployeeRecord;

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

    public EmployeeRecord getItem(Long deviceId) {

        GetItemRequest itemRequest = new GetItemRequest()
                .withTableName(TABLE_NAME)
                .withKey(getItemKey(deviceId));
        AmazonDynamoDB dynamoDB = createClient();
        GetItemResult item = dynamoDB.getItem(itemRequest);

        if (!(item == null)) {
            extractGpiRecord(item);
        }
        return new EmployeeRecord();
    }
    private void extractGpiRecord(GetItemResult item) {

    }

    private Map<String, AttributeValue> getItemKey(Long deviceId) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(DEVICE_ID, new AttributeValue().withN(String.valueOf(deviceId)));
        return new HashMap<>();
    }

    public boolean save(LatestEmployeeRecord recordObject) {
        mapper.save(recordObject);
        return true;
    }

    public boolean save(EmployeeRecord recordObject) {
        mapper.save(recordObject);
        return true;
    }

    public void updateItem(LatestEmployeeRecord record) {

        AmazonDynamoDB client = createClient();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable(TABLE_NAME);

        UpdateItemSpec itemSpec = new UpdateItemSpec();
        itemSpec.withPrimaryKey("empIdCompanyId", record.getEmpIdCompanyId())
                .withUpdateExpression("add #a :intervals set employeeId =:empId, comapanyId =:cID, createdDate =:date")
                .withNameMap(new NameMap().with("#a", "timeIntervals"))
                .withValueMap(new ValueMap().withList(":intervals", record.getTimeIntervals())
                        .withString(":date", record.getCreatedDate()).withString(":empId", record.getEmployeeId())
                        .withString(":compandyId", record.getCompanyId()));

        UpdateItemOutcome updateItemOutcome = table.updateItem(itemSpec);


    }

    public List<String> getByTerminalAndOrgId(Long terminalId, Long companyId, String startDate, String endDate, int i) {

        return getDates(terminalId + "-" + companyId, startDate, endDate, DB_INDEX, i);
    }

    private List<String> getDates(String startDate, String endDate, String globalIndex, String dbIndex, int recordsCount) {

        Map<String, AttributeValue> expressionAttribute = new HashMap<>();
        expressionAttribute.put("deviceIdOrgId", new AttributeValue().withS(globalIndex));
        expressionAttribute.put("employeeId", new AttributeValue().withS(dbIndex));
        expressionAttribute.put("createdDate", new AttributeValue().withS(startDate));
        expressionAttribute.put("createdDate", new AttributeValue().withS(endDate));
        return new ArrayList<String>();
    }
}
