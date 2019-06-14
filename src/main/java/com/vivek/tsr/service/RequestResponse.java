package com.vivek.tsr.service;

import com.vivek.tsr.domain.UserRequest;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;

/**
 * Created by HARSHA on 07-02-2018.
 */
public class RequestResponse {

    private HistoricalDataService historicalDataService;
    private RSVPLastReportedRecord rsvpLastReportedRecord;

    public RequestResponse() {
    }

    public RequestResponse(HistoricalDataService historicalDataService,
                           RSVPLastReportedRecord rsvpLastReportedRecord) {
        this.historicalDataService = historicalDataService;
        this.rsvpLastReportedRecord = rsvpLastReportedRecord;
    }

    public RequestResponse gerRSVPResponse(UserRequest userRequest) {
        if(userRequest.isLastReporting()){
            return rsvpLastReportedRecord.getLastKnowRSVPRecord(userRequest);
        } if(isNullOrEmpty(userRequest.getStartTime()) && isNullOrEmpty(userRequest.getEndTime())){
            return historicalDataService.getHistoricRSVPRecords(userRequest);
        }

        return null;
//                historicalDataService.getHistoricGpiRecords(userRequest);
    }
}
