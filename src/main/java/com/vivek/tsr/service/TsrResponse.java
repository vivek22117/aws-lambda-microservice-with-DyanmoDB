package com.vivek.tsr.service;

import com.amazonaws.util.StringUtils;
import com.vivek.tsr.domain.TSRRequest;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;

/**
 * Created by HARSHA on 07-02-2018.
 */
public class TsrResponse {

    private TsrHistoricDataService tsrHistoricDataService;
    private TsrLastDataService tsrLastDataService;

    public TsrResponse() {
    }

    public TsrResponse(TsrHistoricDataService tsrHistoricDataService, TsrLastDataService tsrLastDataService) {
        this.tsrHistoricDataService = tsrHistoricDataService;
        this.tsrLastDataService = tsrLastDataService;
    }

    public TsrResponse getDsrResponse(TSRRequest tsrRequest) {
        if(tsrRequest.isLastKnown()){
            return tsrLastDataService.getLastKnowGpiRecord(tsrRequest);
        } if(isNullOrEmpty(tsrRequest.getStartTime()) && isNullOrEmpty(tsrRequest.getEndTime())){
            return tsrHistoricDataService.getHistoricGpiRecordsFor24Hours(tsrRequest);
        }

        return null;
//                tsrHistoricDataService.getHistoricGpiRecords(tsrRequest);
    }
}
