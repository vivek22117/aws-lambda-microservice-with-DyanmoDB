package com.vivek.tsr.service;

import com.vivek.tsr.domain.TSRRequest;

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

    public void getDsrResponse(TSRRequest tsrRequest) {
        if(tsrRequest.isLastKnown()){
            tsrLastDataService.getLastKnowGpiRecord(tsrRequest);
        }

        tsrHistoricDataService.getHistoricGpiRecords(tsrRequest);
    }
}
