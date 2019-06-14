package com.vivek.tsr.service;

import com.vivek.tsr.db.DynamoDBOperation;
import com.vivek.tsr.domain.RSVPEventRecord;
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
    private DynamoDBOperation dbOperation;

    public HistoricalDataService(RestService restService, DynamoDBOperation dbOperation) {
        this.restService = restService;
        this.dbOperation = dbOperation;
    }

    public List<RSVPEventRecord> getHistoricGpiRecords(UserRequest userRequest) throws ExecutionException, InterruptedException {
        List<RSVPEventRecord> allRSVPEventRecords = new ArrayList<>();
        RSVPEventRecord RSVPEventRecord;
        userRequest.initialize();

        dbOperation.getByRsvpIdAndTimeIntervals(userRequest.getRsvp_id(), userRequest.getTimeStamp(),
                userRequest.getStartTime(), userRequest.getEndTime());

        List<List<String>> timeStampListForDates = new ArrayList<List<String>>();
        timeStampListForDates.add(reportedDateTimeIntervals1);

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
            List<RSVPEventRecord> modelApiRecords = restService.getModelApiRecords(userRequest, actualTimeInterval);
            allRSVPEventRecords.addAll(modelApiRecords);
            shortFall = getShortFall(userRequest, allRSVPEventRecords);
            if (shortFall == 0) {
                List<RSVPEventRecord> RSVPEventRecordList = allRSVPEventRecords.stream().skip(userRequest.getStartIndex())
                        .limit(userRequest.getCount()).collect(toList());
                userRequest.setStartIndex(0);
                updateTime(userRequest);
                break;
            } else if (shortFall < 0) {
                List<RSVPEventRecord> RSVPEventRecordList = allRSVPEventRecords.stream().skip(userRequest.getStartIndex())
                        .limit(userRequest.getCount()).collect(toList());
                updateStartingIndex(userRequest, shortFall, modelApiRecords);
                //60 + (-50) + 1 =11
            } /*else if (Instant.parse(userRequest.getStartTime()).isBefore(Instant.parse(userRequest.getThreshold()))) {
                List<RSVPEventRecord> gpiRecordList = allRSVPEventRecords.stream().skip(userRequest.getStartIndex())
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


    private void updateStartingIndex(UserRequest userRequest, int shortFall, List<RSVPEventRecord> modelApiRecords) {
        if (modelApiRecords.size() > userRequest.getCount()) {
            userRequest.setStartIndex(modelApiRecords.size() + (shortFall - 1));
        }
    }

    private int getShortFall(UserRequest userRequest, List<RSVPEventRecord> allRSVPEventRecords) {
        return userRequest.getCount() - (allRSVPEventRecords.size() - (userRequest.getStartIndex() - 1));
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

    public RequestResponse getHistoricRSVPRecords(UserRequest userRequest) {
        return null;
    }

}
