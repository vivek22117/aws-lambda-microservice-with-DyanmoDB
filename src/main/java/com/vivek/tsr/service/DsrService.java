package com.vivek.tsr.service;

import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.domain.MyCompany;
import com.vivek.tsr.domain.TSRRequest;
import com.vivek.tsr.utility.PropertyLoader;
import org.joda.time.DateTime;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.vivek.tsr.utility.PropertyLoader.getPropValues;
import static java.lang.Long.parseLong;
import static java.time.Instant.parse;

/**
 * Created by HARSHA on 07-02-2018.
 */
public class DsrService {
    private static final String TIME_INTERVAL = "timeIntervalInMinutes";
    private static final long DEFAULT_TIME = 5;
    private ModelService modelService;

    public DsrService(ModelService modelService) {
        this.modelService = modelService;
    }

    public List<GpiRecord> getHistoricApiRecords(TSRRequest tsrRequest) {
        List<GpiRecord> allGpiRecords = new ArrayList<>();
        GpiRecord gpiRecord = new GpiRecord();
        tsrRequest.initialize(gpiRecord.getEventTime());
        List<MyCompany> myCompanies = createMyCompanies();

        do {
            List<GpiRecord> modelApiRecords = modelService.getModelApiRecords(tsrRequest, myCompanies);
            allGpiRecords.addAll(modelApiRecords);
            if (isApiRecordsValidateCountAndStartIndex(tsrRequest, allGpiRecords)) {
                break;
            }
            updateTime(tsrRequest);
        } while (allGpiRecords.size() < (tsrRequest.getCount() + (tsrRequest.getStartIndex() > 0 ? tsrRequest.getStartIndex() : 0)));

        return new ArrayList<>();

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
}
