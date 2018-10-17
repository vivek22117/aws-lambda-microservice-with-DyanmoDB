package com.vivek.tsr.service;

import com.vivek.tsr.domain.EmployeeRecord;
import com.vivek.tsr.domain.MyCompany;
import com.vivek.tsr.domain.UserRequest;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;

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
public class HistoricalDataService {
    private static final String TIME_INTERVAL = "timeIntervalInMinutes";
    private static final long DEFAULT_TIME = 5;
    private RestService restService;

    private static List<String> reportedDateTimeIntervals1 = Arrays.asList("2018-04-01T02:01:30.610Z", "2018-04-01T10:01:30.610Z");
    private static List<String> reportedDateTimeIntervals2 = Arrays.asList("2018-03-30T12:25:30.514Z", "2018-03-30T09:25:30.505Z", "2018-03-30T23:25:30.420Z", "2018-03-30T07:25:30.514Z", "2018-03-30T13:25:30.505Z", "2018-03-30T20:25:30.420Z");
    private static List<String> reportedDateTimeIntervals3 = Arrays.asList("2018-03-28T17:25:30.415Z", "2018-03-28T15:25:30.561Z", "2018-03-28T20:25:30.500Z");
    private static List<String> reportedDateTimeIntervals4 = Arrays.asList("2018-03-25T09:25:30.120Z", "2018-03-25T11:25:30.111Z", "2018-03-25T23:25:30.111Z");
    private static List<String> reportedDateTimeIntervals5 = Arrays.asList("2018-03-24T07:25:30.012Z", "2018-03-24T13:25:30.222Z", "2018-03-24T10:25:30.215Z", "2018-03-24T15:25:30.222Z", "2018-03-24T17:25:30.215Z");
    private static List<String> reportedDateTimeIntervals6 = Arrays.asList("2018-03-22T11:25:30.610Z", "2018-03-22T22:25:30.333Z", "2018-03-22T23:25:30.351Z");
    private static List<String> reportedDateTimeIntervals7 = Arrays.asList("2018-03-21T05:25:30.610Z", "2018-03-21T22:25:30.333Z", "2018-03-21T15:25:30.351Z");
    private static List<String> reportedDateTimeIntervals8 = Arrays.asList("2018-03-20T05:25:30.610Z", "2018-03-20T22:25:30.333Z", "2018-03-20T15:25:30.351Z");
    private static List<String> reportedDateTimeIntervals9 = Arrays.asList("2018-03-19T05:25:30.610Z", "2018-03-19T22:25:30.333Z", "2018-03-19T15:25:30.351Z");


    public HistoricalDataService(RestService restService) {
        this.restService = restService;
    }

    public List<EmployeeRecord> getHistoricGpiRecords(UserRequest userRequest) throws ExecutionException, InterruptedException {
        List<EmployeeRecord> allEmployeeRecords = new ArrayList<>();
        EmployeeRecord employeeRecord = new EmployeeRecord();
        userRequest.initialize();
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
                    List<String> sortedFilteredTimeIntervals = sortedTimeIntervalofDate(timeList, userRequest);
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
            List<EmployeeRecord> modelApiRecords = restService.getModelApiRecords(userRequest, actualTimeInterval);
            allEmployeeRecords.addAll(modelApiRecords);
            shortFall = getShortFall(userRequest, allEmployeeRecords);
            if (shortFall == 0) {
                List<EmployeeRecord> employeeRecordList = allEmployeeRecords.stream().skip(userRequest.getStartIndex())
                        .limit(userRequest.getCount()).collect(toList());
                userRequest.setStartIndex(0);
                updateTime(userRequest);
                break;
            } else if (shortFall < 0) {
                List<EmployeeRecord> employeeRecordList = allEmployeeRecords.stream().skip(userRequest.getStartIndex())
                        .limit(userRequest.getCount()).collect(toList());
                updateStartingIndex(userRequest, shortFall, modelApiRecords);
                //60 + (-50) + 1 =11
            } /*else if (Instant.parse(userRequest.getStartTime()).isBefore(Instant.parse(userRequest.getThreshold()))) {
                List<EmployeeRecord> gpiRecordList = allEmployeeRecords.stream().skip(userRequest.getStartIndex())
                        .limit(userRequest.getCount()).collect(toList());
                updateStartingIndex(userRequest, shortFall, modelApiRecords);
                updateTime(userRequest);
            }*/
            updateTime(userRequest);
        } while (true);

        return new ArrayList<>();
    }

    public static List<String> sortedTimeIntervalofDate(List<String> timeList, UserRequest userRequest) {
        return timeList.stream().filter(time -> Instant.parse(time).isBefore(Instant.parse(userRequest.getEndTime())))
                .filter(time -> Instant.parse(time).isAfter(Instant.parse(userRequest.getStartTime())))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(String::intern).reversed()).collect(toList());


    }


    private void updateStartingIndex(UserRequest userRequest, int shortFall, List<EmployeeRecord> modelApiRecords) {
        if (modelApiRecords.size() > userRequest.getCount()) {
            userRequest.setStartIndex(modelApiRecords.size() + (shortFall - 1));
        }
    }

    private int getShortFall(UserRequest userRequest, List<EmployeeRecord> allEmployeeRecords) {
        return userRequest.getCount() - (allEmployeeRecords.size() - (userRequest.getStartIndex() - 1));
    }

    private void updateTime(UserRequest userRequest) {
        Instant startTime = parse(userRequest.getStartTime()).minus(parseLong(getPropValues(TIME_INTERVAL)), MINUTES);
        Instant endTime = parse(userRequest.getEndTime()).minus(parseLong(getPropValues(TIME_INTERVAL)), MINUTES);

        userRequest.setStartTime(valueOf(startTime));
        userRequest.setEndTime(valueOf(endTime));
    }

    private List<MyCompany> createMyCompanies() {
        List<MyCompany> myCompanyList = new ArrayList<>();
        myCompanyList.add(buildMyCompany());
        return myCompanyList;
    }

    private MyCompany buildMyCompany() {
        MyCompany myCompany = new MyCompany();
        myCompany.setId("1001");
        myCompany.setCompanyName("IT Services");
        myCompany.setCompanyBeginDate(valueOf(Instant.now()));
        myCompany.setCompanyEndDate(valueOf(Instant.now().minus(DEFAULT_TIME, DAYS)));
        myCompany.setActive(true);
        return myCompany;
    }

    public RequestResponse getHistoricGpiRecordsFor24Hours(UserRequest userRequest) {
        return null;
    }

    public static void main(String[] args) {

//        List<String> timeList = Arrays.asList("2018-03-30T12:25:30.514Z", "2018-03-30T23:25:30.505Z", "2018-03-30T23:25:30.420Z", "2018-03-30T07:25:30.514Z", "2018-03-30T13:25:30.505Z", "2018-03-30T20:25:30.420Z");

        UserRequest userRequest = new UserRequest();
        userRequest.setStartTime("2018-03-19T23:00:05.105Z");
        userRequest.setEndTime("2018-04-01T07:05:05.105Z");
        userRequest.setEmployeeId(5055L);
        userRequest.setContentType("application/json");

        /*List<String> strings = sortedTimeIntervalofDate(timeList, userRequest);
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
                    List<String> sortedFilteredTimeIntervals = sortedTimeIntervalofDate(timeList, userRequest);
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
