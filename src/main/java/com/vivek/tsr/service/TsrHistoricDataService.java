package com.vivek.tsr.service;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.domain.MyCompany;
import com.vivek.tsr.domain.TSRRequest;
import com.vivek.tsr.utility.PropertyLoader;
import org.joda.time.DateTime;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.vivek.tsr.utility.AppUtil.getFormattedDate;
import static com.vivek.tsr.utility.AppUtil.getNumberOfIntervals;
import static com.vivek.tsr.utility.PropertyLoader.getPropValues;
import static java.lang.Long.parseLong;
import static java.lang.String.*;
import static java.time.Duration.between;
import static java.time.Instant.parse;
import static java.time.temporal.ChronoUnit.*;
import static java.util.stream.Collectors.*;

/**
 * Created by Vivek Kumar Mishra on 07-02-2018.
 */
public class TsrHistoricDataService {
    private static final String TIME_INTERVAL = "timeIntervalInMinutes";
    private static final long DEFAULT_TIME = 5;
    private ModelService modelService;

    private static List<String> reportedDateTimeIntervals1 = Arrays.asList("2018-04-01T02:01:30.610Z", "2018-04-01T10:01:30.610Z");
    private static List<String> reportedDateTimeIntervals2 = Arrays.asList("2018-03-30T12:25:30.514Z", "2018-03-30T09:25:30.505Z", "2018-03-30T23:25:30.420Z", "2018-03-30T07:25:30.514Z", "2018-03-30T13:25:30.505Z", "2018-03-30T20:25:30.420Z");
    private static List<String> reportedDateTimeIntervals3 = Arrays.asList("2018-03-28T17:25:30.415Z", "2018-03-28T15:25:30.561Z", "2018-03-28T20:25:30.500Z");
    private static List<String> reportedDateTimeIntervals4 = Arrays.asList("2018-03-25T09:25:30.120Z", "2018-03-25T11:25:30.111Z", "2018-03-25T23:25:30.111Z");
    private static List<String> reportedDateTimeIntervals5 = Arrays.asList("2018-03-24T07:25:30.012Z", "2018-03-24T13:25:30.222Z", "2018-03-24T10:25:30.215Z", "2018-03-24T15:25:30.222Z", "2018-03-24T17:25:30.215Z");
    private static List<String> reportedDateTimeIntervals6 = Arrays.asList("2018-03-22T11:25:30.610Z", "2018-03-22T22:25:30.333Z", "2018-03-22T23:25:30.351Z");
    private static List<String> reportedDateTimeIntervals7 = Arrays.asList("2018-03-21T05:25:30.610Z", "2018-03-21T22:25:30.333Z", "2018-03-21T15:25:30.351Z");
    private static List<String> reportedDateTimeIntervals8 = Arrays.asList("2018-03-20T05:25:30.610Z", "2018-03-20T22:25:30.333Z", "2018-03-20T15:25:30.351Z");
    private static List<String> reportedDateTimeIntervals9 = Arrays.asList("2018-03-19T05:25:30.610Z", "2018-03-19T22:25:30.333Z", "2018-03-19T15:25:30.351Z");


    public TsrHistoricDataService(ModelService modelService) {
        this.modelService = modelService;
    }

    public List<GpiRecord> getHistoricGpiRecords(TSRRequest tsrRequest) throws ExecutionException, InterruptedException {
        List<GpiRecord> allGpiRecords = new ArrayList<>();
        GpiRecord gpiRecord = new GpiRecord();
        tsrRequest.initialize();
        List<List<String>> timeStampListForDates = new ArrayList<List<String>>();
        timeStampListForDates.add(reportedDateTimeIntervals1);
        timeStampListForDates.add(reportedDateTimeIntervals2);
        timeStampListForDates.add(reportedDateTimeIntervals3);
        timeStampListForDates.add(reportedDateTimeIntervals4);
        timeStampListForDates.add(reportedDateTimeIntervals5);
        timeStampListForDates.add(reportedDateTimeIntervals6);
        timeStampListForDates.add(reportedDateTimeIntervals7);
        timeStampListForDates.add(reportedDateTimeIntervals8);
        timeStampListForDates.add(reportedDateTimeIntervals9);

        List<String> actualTimeInterval = new ArrayList<>();

        timeStampListForDates.stream()
                .forEach(timeList -> {
                    List<String> sortedFilteredTimeIntervals = sortedTimeIntervalofDate(timeList, tsrRequest);
                    if (sortedFilteredTimeIntervals.size() >= 1) {
                        if (between(parse(sortedFilteredTimeIntervals.get(sortedFilteredTimeIntervals.size() - 1)), parse(sortedFilteredTimeIntervals.get(0))).getSeconds() > 43200) {
                            actualTimeInterval.add(sortedFilteredTimeIntervals.get(0) + "=" + valueOf(Instant.parse(sortedFilteredTimeIntervals.get(0)).minus(720, MINUTES)));
                            actualTimeInterval.add(valueOf(Instant.parse(sortedFilteredTimeIntervals.get(0)).minus(43201, ChronoUnit.SECONDS)) + "=" + sortedFilteredTimeIntervals.get(sortedFilteredTimeIntervals.size() - 1));
                        } else {
                            actualTimeInterval.add(sortedFilteredTimeIntervals.get(0) + "=" + sortedFilteredTimeIntervals.get(sortedFilteredTimeIntervals.size() - 1));
                        }
                    }
                });

        System.out.println(actualTimeInterval);
//        List<MyCompany> myCompanies = createMyCompanies();
        int shortFall = 0;
        do {
           /* List<String> timeIntervals = reportedDates.stream().map(data -> getFormattedDate(data)).map(d1 -> getNumberOfIntervals(d1))
                    .flatMap(x -> x.stream()).collect(toList());*/
            List<GpiRecord> modelApiRecords = modelService.getModelApiRecords(tsrRequest, actualTimeInterval);
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
            } /*else if (Instant.parse(tsrRequest.getStartTime()).isBefore(Instant.parse(tsrRequest.getThreshold()))) {
                List<GpiRecord> gpiRecordList = allGpiRecords.stream().skip(tsrRequest.getStartIndex())
                        .limit(tsrRequest.getCount()).collect(toList());
                updateStartingIndex(tsrRequest, shortFall, modelApiRecords);
                updateTime(tsrRequest);
            }*/
            updateTime(tsrRequest);
        } while (true);

        return new ArrayList<>();
    }

    public static List<String> sortedTimeIntervalofDate(List<String> timeList, TSRRequest tsrRequest) {
        return timeList.stream().filter(time -> Instant.parse(time).isBefore(Instant.parse(tsrRequest.getEndTime())))
                .filter(time -> Instant.parse(time).isAfter(Instant.parse(tsrRequest.getStartTime())))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(String::intern).reversed()).collect(toList());


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
        Instant startTime = parse(tsrRequest.getStartTime()).minus(parseLong(getPropValues(TIME_INTERVAL)), MINUTES);
        Instant endTime = parse(tsrRequest.getEndTime()).minus(parseLong(getPropValues(TIME_INTERVAL)), MINUTES);

        tsrRequest.setStartTime(valueOf(startTime));
        tsrRequest.setEndTime(valueOf(endTime));
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
        myCompany.setBeginTime(valueOf(Instant.now()));
        myCompany.setBeginTime(valueOf(Instant.now().minus(DEFAULT_TIME, DAYS)));
        myCompany.setActive(true);
        return myCompany;
    }

    public TsrResponse getHistoricGpiRecordsFor24Hours(TSRRequest tsrRequest) {
        return null;
    }

    public static void main(String[] args) {

//        List<String> timeList = Arrays.asList("2018-03-30T12:25:30.514Z", "2018-03-30T23:25:30.505Z", "2018-03-30T23:25:30.420Z", "2018-03-30T07:25:30.514Z", "2018-03-30T13:25:30.505Z", "2018-03-30T20:25:30.420Z");

        TSRRequest tsrRequest = new TSRRequest();
        tsrRequest.setStartTime("2018-03-19T23:00:05.105Z");
        tsrRequest.setEndTime("2018-04-01T07:05:05.105Z");
        tsrRequest.setTerminalId(5055L);
        tsrRequest.setOrgId("-10000");
        tsrRequest.setContentType("tsr-content-type");

        /*List<String> strings = sortedTimeIntervalofDate(timeList, tsrRequest);
        System.out.println(strings);*/


        List<List<String>> timeStampListForDates = new ArrayList<List<String>>();
        timeStampListForDates.add(reportedDateTimeIntervals1);
        timeStampListForDates.add(reportedDateTimeIntervals2);
        timeStampListForDates.add(reportedDateTimeIntervals3);
        timeStampListForDates.add(reportedDateTimeIntervals4);
        timeStampListForDates.add(reportedDateTimeIntervals5);
        timeStampListForDates.add(reportedDateTimeIntervals6);
        timeStampListForDates.add(reportedDateTimeIntervals7);
        timeStampListForDates.add(reportedDateTimeIntervals8);
        timeStampListForDates.add(reportedDateTimeIntervals9);

        List<String> actualTimeInterval = new ArrayList<>();

        timeStampListForDates.stream()
                .forEach(timeList -> {
                    List<String> sortedFilteredTimeIntervals = sortedTimeIntervalofDate(timeList, tsrRequest);
                    if (sortedFilteredTimeIntervals.size() >= 1) {
                        if (between(parse(sortedFilteredTimeIntervals.get(sortedFilteredTimeIntervals.size() - 1)), parse(sortedFilteredTimeIntervals.get(0))).getSeconds() > 43200) {
                            actualTimeInterval.add(sortedFilteredTimeIntervals.get(0) + "=" + valueOf(Instant.parse(sortedFilteredTimeIntervals.get(0)).minus(720, MINUTES)));
                            actualTimeInterval.add(valueOf(Instant.parse(sortedFilteredTimeIntervals.get(0)).minus(43201, ChronoUnit.SECONDS)) + "=" + sortedFilteredTimeIntervals.get(sortedFilteredTimeIntervals.size() - 1));
                        } else {
                            actualTimeInterval.add(sortedFilteredTimeIntervals.get(0) + "=" + sortedFilteredTimeIntervals.get(sortedFilteredTimeIntervals.size() - 1));
                        }
                    }
                });

        System.out.println(actualTimeInterval);
    }
}
