package com.vivek.tsr.service;

import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.domain.MyCompany;
import com.vivek.tsr.domain.TSRRequest;
import com.vivek.tsr.utility.PropertyLoader;
import org.joda.time.DateTime;


import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.vivek.tsr.utility.PropertyLoader.getPropValues;
import static java.lang.Long.parseLong;
import static java.time.Instant.parse;
import static java.util.stream.Collectors.*;

/**
 * Created by HARSHA on 07-02-2018.
 */
public class TsrHistoricDataService {
    private static final String TIME_INTERVAL = "timeIntervalInMinutes";
    private static final long DEFAULT_TIME = 5;
    private ModelService modelService;

    public TsrHistoricDataService(ModelService modelService) {
        this.modelService = modelService;
    }

    public List<GpiRecord> getHistoricGpiRecords(TSRRequest tsrRequest) {
        List<GpiRecord> allGpiRecords = new ArrayList<>();
        GpiRecord gpiRecord = new GpiRecord();
        tsrRequest.initialize(gpiRecord.getEventTime());
        List<MyCompany> myCompanies = createMyCompanies();
        int shortFall = 0;
        do {
            List<GpiRecord> modelApiRecords = modelService.getModelApiRecords(tsrRequest, myCompanies);
            allGpiRecords.addAll(modelApiRecords);
            shortFall = getShortFall(tsrRequest, allGpiRecords);
            if (shortFall == 0) {
                List<GpiRecord> gpiRecordList = allGpiRecords.stream().skip(tsrRequest.getStartIndex())
                        .limit(tsrRequest.getCount()).collect(toList());
                tsrRequest.setStartIndex(0);
                updateTime(tsrRequest);
                break;
            } else if (shortFall < 0) {
                List<GpiRecord> gpiRecordList = allGpiRecords.stream().skip(tsrRequest.getStartIndex())
                        .limit(tsrRequest.getCount()).collect(toList());
                updateStartingIndex(tsrRequest, shortFall, modelApiRecords);
                //60 + (-50) + 1 =11
            } else if (Instant.parse(tsrRequest.getStartTime()).isBefore(Instant.parse(tsrRequest.getThreshold()))) {
                updateStartingIndex(tsrRequest, shortFall, modelApiRecords);
                updateTime(tsrRequest);
            }
            updateTime(tsrRequest);
        } while (true);

        return new ArrayList<>();
    }

    private void updateStartingIndex(TSRRequest tsrRequest, int shortFall, List<GpiRecord> modelApiRecords) {
        if (modelApiRecords.size() > tsrRequest.getCount()) {
            tsrRequest.setStartIndex(modelApiRecords.size() + (shortFall - 1));
        }
    }

    private int getShortFall(TSRRequest tsrRequest, List<GpiRecord> allGpiRecords) {
        return tsrRequest.getCount() - (allGpiRecords.size() - (tsrRequest.getStartIndex() - 1));
    }

    private void updateTime(TSRRequest tsrRequest) {
        Instant startTime = parse(tsrRequest.getStartTime()).minus(parseLong(getPropValues(TIME_INTERVAL)), ChronoUnit.MINUTES);
        Instant endTime = parse(tsrRequest.getEndTime()).minus(parseLong(getPropValues(TIME_INTERVAL)), ChronoUnit.MINUTES);

        tsrRequest.setStartTime(String.valueOf(startTime));
        tsrRequest.setEndTime(String.valueOf(endTime));
    }

    private boolean isApiRecordsValidateCountAndStartIndex(TSRRequest tsrRequest, List<GpiRecord> allGpiRecords) {
        return allGpiRecords.size() >= tsrRequest.getCount() && (allGpiRecords.size() >= tsrRequest.getStartIndex()
                && (allGpiRecords.size() >= (tsrRequest.getCount() + tsrRequest.getStartIndex() > 0 ? tsrRequest.getStartIndex() - 1 : 0)));
    }

    private List<MyCompany> createMyCompanies() {
        List<MyCompany> myCompanyList = new ArrayList<>();
        myCompanyList.add(buildMyCompany());
        return myCompanyList;
    }

    private MyCompany buildMyCompany() {
        MyCompany myCompany = new MyCompany();
        myCompany.setId("1001");
        myCompany.setMyOrgId("5001");
        myCompany.setDeviceId(2001L);
        myCompany.setBeginTime(String.valueOf(Instant.now()));
        myCompany.setBeginTime(String.valueOf(Instant.now().minus(DEFAULT_TIME, ChronoUnit.DAYS)));
        myCompany.setActive(true);
        return myCompany;
    }

    public void getLastKnowGpiRecord(TSRRequest tsrRequest) {

    }
}
