package com.vivek.tsr.service;

import com.vivek.tsr.domain.TSRRequest;

/**
 * Created by HARSHA on 07-02-2018.
 */
public class TsrResponse {

    private DsrService dsrService;

    public TsrResponse() {
    }

    public TsrResponse(DsrService dsrService) {
        this.dsrService = dsrService;
    }

    public void getDsrResponse(TSRRequest tsrRequest) {

        dsrService.getHistoricApiRecords(tsrRequest);
    }
}
