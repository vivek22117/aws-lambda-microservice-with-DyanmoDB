package com.vivek.tsr.service;

import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.domain.MyCompany;
import com.vivek.tsr.domain.TSRRequest;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HARSHA on 07-02-2018.
 */
public class DsrService {
    private ModelService modelService;

    public DsrService(ModelService modelService) {
        this.modelService = modelService;
    }

    public List<GpiRecord> getHistoricApiRecords(TSRRequest tsrRequest) {
        GpiRecord gpiRecord = new GpiRecord();
        tsrRequest.initialize(gpiRecord.getEventTime());
        List<MyCompany> myCompanies = createMyCompanies();

            modelService.getModelApiRecords(tsrRequest, myCompanies);

        return new ArrayList<>();

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
        myCompany.setBeginTime(String.valueOf(DateTime.now()));
        myCompany.setBeginTime(String.valueOf(Instant.now().minus(100000L)));
        myCompany.setActive(true);
        return myCompany;
    }
}
