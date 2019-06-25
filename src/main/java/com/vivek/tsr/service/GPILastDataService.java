package com.vivek.tsr.service;

import com.vivek.tsr.db.DynamoDBOperation;
import com.vivek.tsr.domain.UserRequest;
import com.vivek.tsr.utility.AppUtil;

import java.util.List;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;

public interface GPILastDataService {

    int ddbLimit = 1;

    default List<String> getLastReportedDateFromDDB(UserRequest userRequest, DynamoDBOperation dbOperation) {
        if (!isNullOrEmpty(userRequest.getStartTime()) && !isNullOrEmpty(userRequest.getEndTime())) {
            dbOperation.getEventByRSVPAndEventId(userRequest.getRsvp_id(), userRequest.getEvent_id(), ddbLimit);
        }

        return null;
    }
}
