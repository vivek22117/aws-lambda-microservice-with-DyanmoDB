package com.vivek.tsr.service;

import com.vivek.tsr.domain.TSRRequest;
import com.vivek.tsr.utility.AppUtil;

import java.util.List;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;
import static java.util.stream.Collectors.toList;

/**
 * Created by HARSHA on 28-02-2018.
 */
public class TsrLastDataService implements GPILastDataService{

    private static final int DDB_LIMIT = 1;
    private DynamoDBOperation dynamoDBOperation;

    public TsrLastDataService(DynamoDBOperation dynamoDBOperation) {
        this.dynamoDBOperation = dynamoDBOperation;
    }


    public TsrResponse getLastKnowGpiRecord(TSRRequest tsrRequest) {

        List<String> reportedDates = getLastReportedDateFromDDB(tsrRequest, dynamoDBOperation);
        List<String> stringIntervals = reportedDates.stream().map(AppUtil::getFormattedDate)
                .map(AppUtil::getNumberOfIntervals).flatMap(List::stream).collect(toList());


        return null;
    }
}
