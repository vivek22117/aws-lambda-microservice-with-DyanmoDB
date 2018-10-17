package com.vivek.tsr.service;

import com.vivek.tsr.domain.UserRequest;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;

/**
 * Created by HARSHA on 07-02-2018.
 */
public class RequestResponse {

    private HistoricalDataService historicalDataService;
    private TsrLastDataService tsrLastDataService;

    public RequestResponse() {
    }

    public RequestResponse(HistoricalDataService historicalDataService, TsrLastDataService tsrLastDataService) {
        this.historicalDataService = historicalDataService;
        this.tsrLastDataService = tsrLastDataService;
    }

    public RequestResponse getDsrResponse(UserRequest userRequest) {
        if(userRequest.isLastReporting()){
            return tsrLastDataService.getLastKnowGpiRecord(userRequest);
        } if(isNullOrEmpty(userRequest.getStartTime()) && isNullOrEmpty(userRequest.getEndTime())){
            return historicalDataService.getHistoricGpiRecordsFor24Hours(userRequest);
        }

        return null;
//                historicalDataService.getHistoricGpiRecords(userRequest);
    }
}
