package com.vivek.tsr.service;

import com.vivek.tsr.domain.RSVPRequest;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;

public class APIResponse {

    private HistoricalDataService historicalDataService;
    private RSVPLastReportedRecord rsvpLastReportedRecord;

    public APIResponse() {
        this(new HistoricalDataService(), new RSVPLastReportedRecord());
    }

    public APIResponse(HistoricalDataService historicalDataService,
                       RSVPLastReportedRecord rsvpLastReportedRecord) {
        this.historicalDataService = historicalDataService;
        this.rsvpLastReportedRecord = rsvpLastReportedRecord;
    }

    public APIResponse gerRSVPResponse(RSVPRequest rsvpRequest) {
        if(rsvpRequest.isLastReporting()){
            return rsvpLastReportedRecord.getLastKnowRSVPRecord(rsvpRequest);
        } if(isNullOrEmpty(rsvpRequest.getStartTime()) && isNullOrEmpty(rsvpRequest.getEndTime())){
            return historicalDataService.getHistoricRSVPRecords(rsvpRequest);
        }

        return null;
//                historicalDataService.getHistoricGpiRecords(userRequest);
    }
}
