package com.vivek.tsr.db;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.vivek.tsr.domain.RSVPEventRecord;
import com.vivek.tsr.entity.LatestRSVPRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HARSHA on 20-02-2018.
 */
public class DynamoDBOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDBOperation.class);

    private static final String TABLE_NAME = "RSVPEventTable";
    private static final String RSVP_ID = "rsvpId";
    private static final String EVENT_ID = "eventId";
    private DynamoDBMapper mapper;

    public DynamoDBOperation() {
        this.mapper = new DynamoDBMapper(createClient());
    }

    public void save(LatestRSVPRecord recordObject) {
        mapper.save(recordObject);
        LOGGER.debug("Record persisted successfully in DynamoDB");
    }

    public RSVPEventRecord getItem(String rsvpId) {

        GetItemRequest itemRequest = new GetItemRequest()
                .withTableName(TABLE_NAME)
                .withKey(getItemKey(rsvpId));
        AmazonDynamoDB dynamoDB = createClient();
        GetItemResult item = dynamoDB.getItem(itemRequest);

        if (!(item == null)) {
            extractGpiRecord(item);
        }
        return null;
    }

    private void extractGpiRecord(GetItemResult item) {

    }

    public List<String> getByRsvpIdAndTimeIntervals(Integer rsvpId, String startDate, String endDate) {
        return getLastReportedRsvp(rsvpId, startDate, endDate, EVENT_ID, 10);
    }

    private Map<String, AttributeValue> getItemKey(String rsvpId) {
        Map<String, AttributeValue> itemToFetch = new HashMap<>();
        itemToFetch.put(RSVP_ID, new AttributeValue().withS(rsvpId));
        return itemToFetch;
    }

    private List<String> getLastReportedRsvp(Integer rsvpId, String startDate, String endDate,
                                             String eventId, int recordsCount) {

        Map<String, AttributeValue> expressionAttribute = new HashMap<>();
        expressionAttribute.put("rsvpId", new AttributeValue().withS(String.valueOf(rsvpId)));
        expressionAttribute.put("rsvpMakeTime", new AttributeValue().withS(endDate));
        expressionAttribute.put("createdDate", new AttributeValue().withS(endDate));
        return new ArrayList<String>();
    }

    private final AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-east-1")
            .withCredentials(new InstanceProfileCredentialsProvider(true));

    private AmazonDynamoDB createClient() {
        return builder.build();
    }
}
