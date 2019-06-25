package com.vivek.tsr.service;

import com.amazonaws.util.CollectionUtils;
import com.vivek.tsr.domain.RSVPEventRecord;
import com.vivek.tsr.domain.UserRequest;
import com.vivek.tsr.utility.JsonUtility;
import com.vivek.tsr.utility.PropertyLoader;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import static java.time.Instant.parse;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Vivek Kumar Mishra on 07-02-2018.
 */
public class RestService {

    private static Logger logger;

    private static final String MY_MODEL_API = "myRestApi";
    private static final String MY_DATA_API = "myDataApi";
    private static final String DATA_API = "/getByEmployeeIdAndCompanyId/";
    private static final String DELIMITER = "&";

    private RestTemplate restTemplate;
    private JsonUtility jsonUtility;

    public RestService() {
        this(new RestTemplate(), new JsonUtility(), getLogger(RestService.class));
    }

    public RestService(RestTemplate restTemplate, JsonUtility jsonUtility, Logger logger) {
        this.restTemplate = restTemplate;
        this.jsonUtility = jsonUtility;
        this.logger = logger;
    }


    public List<RSVPEventRecord> getModelApiRecords(UserRequest userRequest, List<String> timeIntervalsInString) throws ExecutionException, InterruptedException {

//        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        List<RSVPEventRecord> allRSVPEventRecords = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(4);

        pool.submit(() -> timeIntervalsInString.parallelStream().map(interval -> {
            try {
                return getModelApiRecordFromMyModelService(userRequest.getRsvp_id(), setStartTime(interval, userRequest),
                        setEndTIme(interval, userRequest));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ArrayList<RSVPEventRecord>();
        }).flatMap(List::stream).collect(toList())).get();

       /* pool.submit(()->timeIntervals.parallelStream().forEach(interval ->{
            try {
                allRSVPEventRecords.addAll(getModelApiRecordFromMyModelService(userRequest.getTerminalId(),userRequest.getOrgId()
                        ,setStartTime(interval,userRequest),setEndTIme(interval,userRequest)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).get();*/
      /*  List<Future<List<RSVPEventRecord>>> collect1 = myCompanies.parallelStream().map(myCompany -> executorService.submit(new Callable<List<RSVPEventRecord>>() {
            @Override
            public List<RSVPEventRecord> call() throws Exception {
                return getModelApiRecordFromMyModelService(userRequest.getTerminalId(), myCompany.getMyOrgId(),
                        userRequest.getStartTime(), userRequest.getEndTime());
            }
        })).collect(toList());*/


//        timeIntervals.stream().map()


       /* List<RSVPEventRecord> collect1 = timeIntervals.parallelStream().map(t1 -> {
            try {
                return executorService.submit(new Callable<List<RSVPEventRecord>>() {
                    @Override
                    public List<RSVPEventRecord> call() throws Exception {
                        userRequest.initialize(t1);
                        return getModelApiRecordFromMyModelService(userRequest.getTerminalId(), userRequest.getOrgId(),
                                userRequest.getStartTime(), userRequest.getEndTime());
                    }
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return new ArrayList<RSVPEventRecord>();
        }).flatMap(List::stream).collect(toList());*/

//                .stream().map(Future::get);

       /* collect.parallelStream()
                .map(time -> executorService.submit(() ->
                        getModelApiRecordFromMyModelService(userRequest.getTerminalId(), userRequest.getOrgId(),
                                userRequest.getStartTime(), userRequest.getEndTime()))).collect(Collectors.toList());
*/
       /* List<Future<List<RSVPEventRecord>>> futureList = myCompanies.parallelStream()
                .filter(myCompany -> myCompany.isActive())
                .map(myCompany -> executorService.submit(() ->
                        getModelApiRecordFromMyModelService(userRequest.getTerminalId(), myCompany.getMyOrgId(),
                                userRequest.getStartTime(), userRequest.getEndTime()))).collect(Collectors.toList());
*/

      /*  List<RSVPEventRecord> collectedGpiRecords = futureList.parallelStream().map(listFuture -> {
            try {
                return listFuture.get();
            } catch (InterruptedException | ExecutionException ex) {
                logger.error("Unable to get gpiRecords for future list: ", ex);
            }
            return new ArrayList<RSVPEventRecord>();
        }).flatMap(List::stream).collect(Collectors.toList());*/

        if (CollectionUtils.isNullOrEmpty(allRSVPEventRecords)) {
            return null;
        }
        pool.shutdown();
        sort(allRSVPEventRecords, Comparator.comparing(RSVPEventRecord::getMtime));
        return allRSVPEventRecords;
    }

    /*private Function<String, List<RSVPEventRecord>> fetchModelApiRecords(String interval, UserRequest tsrRequest) throws IOException {
        return interval -> {
            getModelApiRecordFromMyModelService(tsrRequest.getTerminalId(),tsrRequest.getOrgId(),
                    setStartTime(interval,tsrRequest),setEndTIme(interval,tsrRequest));
        }
    }*/

    private String setEndTIme(String interval, UserRequest userRequest) {
        String[] splitedIntervals = interval.split("=");
        String endTime = splitedIntervals[0];
        System.out.println(endTime);
        if (parse(userRequest.getEndTime()).isAfter(parse(endTime))) {
            return endTime;
        }
        return userRequest.getEndTime();
    }

    private String setStartTime(String interval, UserRequest userRequest) {
        String[] splitedTimeIntervals = interval.split("=");
        String startTime = splitedTimeIntervals[1];
        System.out.println(startTime);
        if (parse(startTime).isAfter(parse(userRequest.getStartTime()))) {
            return startTime;
        }
        return userRequest.getStartTime();
    }

    private List<RSVPEventRecord> getModelApiRecordFromMyModelService(Integer rsvpId, String startTime, String endTime) throws IOException {
        List<RSVPEventRecord> modelRSVPEventRecord = new ArrayList<>();
        String url = buildMyModelServiceURLForTerminalData(rsvpId, startTime, endTime);

        System.out.println(url);
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        RSVPEventRecord RSVPEventRecord = new JsonUtility().converCollectionFromJson(forEntity.getBody(), RSVPEventRecord.class);
        modelRSVPEventRecord.add(RSVPEventRecord);

        return modelRSVPEventRecord;
    }

    private String buildMyModelServiceURLForTerminalData(Integer terminalId, String startTime, String endTime) {

        return String.valueOf(new StringBuilder("http://my.vivek.com").append(MY_DATA_API).append(DATA_API)
                .append("?").append("employeeId=").append(terminalId).append(DELIMITER)
                .append(DELIMITER).append("startTime=").append(startTime)
                .append(DELIMITER).append("endTime=").append(endTime));
    }

    private String getMyServiceApi() {
        return PropertyLoader.getPropValues(MY_MODEL_API);
    }

    private static UserRequest getTsrReqest() {
        UserRequest userRequest = new UserRequest();
        userRequest.setCount(100);
        userRequest.setStartIndex(0);
        userRequest.setRsvp_id(5001);
        userRequest.setContentType("application/json");
        userRequest.setStartTime("2018-02-21T05:00:05.201Z");
        userRequest.setEndTime("2018-04-01T07:05:05.101Z");
        return userRequest;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RestService restService = new RestService();
        List<String> timeIntervals = Arrays.asList("2018-04-01T02:01:30.610Z=2018-04-01T02:01:30.610Z",
                "2018-03-30T23:25:30.420Z=2018-03-30T11:25:30.420Z", "2018-03-30T11:25:29.420Z=2018-03-30T07:25:30.514Z",
                "2018-03-28T20:25:30.500Z=2018-03-28T15:25:30.561Z", "2018-03-25T23:25:30.111Z=2018-03-25T11:25:30.111Z", "2018-03-25T11:25:29.111Z=2018-03-25T09:25:30.120Z",
                "2018-03-24T17:25:30.215Z=2018-03-24T07:25:30.012Z", "2018-03-22T22:25:30.333Z=2018-03-22T10:25:30.333Z", "2018-03-22T10:25:29.333Z=2018-03-22T05:25:30.610Z",
                "2018-03-21T22:25:30.333Z=2018-03-21T10:25:30.333Z", "2018-03-21T10:25:29.333Z=2018-03-21T05:25:30.610Z");
        UserRequest userRequest = getTsrReqest();
        restService.getModelApiRecords(userRequest, timeIntervals);
    }
}
