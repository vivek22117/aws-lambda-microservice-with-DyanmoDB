package com.vivek.tsr.service;

import com.amazonaws.util.StringUtils;
import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.domain.TSRRequest;
import com.vivek.tsr.utility.AppUtil;
import sun.security.timestamp.TSRequest;

import java.util.List;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;

/**
 * Created by HARSHA on 28-02-2018.
 */
public interface GPILastDataService {

    int ddbLimit = 1;

    default List<String> getLastReportedDateFromDDB(TSRRequest tsRequest, DynamoDBOperation dbOperation) {
        if (!isNullOrEmpty(tsRequest.getStartTime()) && !isNullOrEmpty(tsRequest.getEndTime())) {
            dbOperation.getByTerminalAndOrgId(tsRequest.getTerminalId(), tsRequest.getOrgId(),
                    AppUtil.getTimeStampToDate(tsRequest.getStartTime()), AppUtil.getTimeStampToDate(tsRequest.getEndTime()), 1);
        }

        return null;
    }
}
