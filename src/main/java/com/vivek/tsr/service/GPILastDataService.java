package com.vivek.tsr.service;

import com.amazonaws.util.StringUtils;
import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.domain.TSRRequest;
import sun.security.timestamp.TSRequest;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;

/**
 * Created by HARSHA on 28-02-2018.
 */
public interface GPILastDataService {


    default GpiRecord getLastReportedDateFromDDB(TSRRequest tsRequest, DynamoDBOperation dbOperation){
        if(!isNullOrEmpty(tsRequest.getStartTime()) && !isNullOrEmpty(tsRequest.getEndTime())){
            dbOperation.getByTerminalAndOrgId(tsRequest.getTerminalId(),tsRequest.getOrgId(),1);
        }

     return null;
    }
}
