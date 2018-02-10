package com.vivek.tsr.service;

import com.vivek.tsr.domain.TSRRequest;

/**
 * Created by HARSHA on 07-02-2018.
 */
public class DsrResponse {

    private DsrService dsrService;

    public DsrResponse(DsrService dsrService) {
        this.dsrService = dsrService;
    }

    public void getDsrResponse(TSRRequest tsrRequest) {

        dsrService.getHistoricApiRecords(tsrRequest);
    }
}
