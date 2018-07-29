package com.vivek.tsr.service;

import com.amazonaws.util.CollectionUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.domain.MyCompany;
import com.vivek.tsr.domain.TSRRequest;
import com.vivek.tsr.utility.JsonUtility;
import com.vivek.tsr.utility.PropertyLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.Instant.parse;
import static java.util.Collections.enumeration;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Vivek Kumar Mishra on 07-02-2018.
 */
public class ModelService {

    private static Logger logger;

    private static final String MY_MODEL_API = "myModelApi";
    private static final String MY_DATA_API = "myDataApi";
    private static final String DATA_API = "/getByTypeAndOrgId/";
    private static final String DELIMITER = "&";

    private RestTemplate restTemplate;
    private JsonUtility jsonUtility;

    public ModelService() {
        this(new RestTemplate(), new JsonUtility(), getLogger(ModelService.class));
    }

    public ModelService(RestTemplate restTemplate, JsonUtility jsonUtility, Logger logger) {
        this.restTemplate = restTemplate;
        this.jsonUtility = jsonUtility;
        this.logger = logger;
    }





    public List<GpiRecord> getModelApiRecords(TSRRequest tsrRequest, List<String> timeIntervalsInString) throws ExecutionException, InterruptedException {

//        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        List<GpiRecord> allGpiRecords = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(4);

        pool.submit(() -> timeIntervalsInString.parallelStream().map(interval -> {
            try {
                return getModelApiRecordFromMyModelService(tsrRequest.getTerminalId(), tsrRequest.getOrgId()
                        , setStartTime(interval,tsrRequest), setEndTIme(interval,tsrRequest));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ArrayList<GpiRecord>();
        }).flatMap(List::stream).collect(toList())).get();

       /* pool.submit(()->timeIntervals.parallelStream().forEach(interval ->{
            try {
                allGpiRecords.addAll(getModelApiRecordFromMyModelService(tsrRequest.getTerminalId(),tsrRequest.getOrgId()
                        ,setStartTime(interval,tsrRequest),setEndTIme(interval,tsrRequest)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).get();*/
      /*  List<Future<List<GpiRecord>>> collect1 = myCompanies.parallelStream().map(myCompany -> executorService.submit(new Callable<List<GpiRecord>>() {
            @Override
            public List<GpiRecord> call() throws Exception {
                return getModelApiRecordFromMyModelService(tsrRequest.getTerminalId(), myCompany.getMyOrgId(),
                        tsrRequest.getStartTime(), tsrRequest.getEndTime());
            }
        })).collect(toList());*/


//        timeIntervals.stream().map()


       /* List<GpiRecord> collect1 = timeIntervals.parallelStream().map(t1 -> {
            try {
                return executorService.submit(new Callable<List<GpiRecord>>() {
                    @Override
                    public List<GpiRecord> call() throws Exception {
                        tsrRequest.initialize(t1);
                        return getModelApiRecordFromMyModelService(tsrRequest.getTerminalId(), tsrRequest.getOrgId(),
                                tsrRequest.getStartTime(), tsrRequest.getEndTime());
                    }
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return new ArrayList<GpiRecord>();
        }).flatMap(List::stream).collect(toList());*/

//                .stream().map(Future::get);

       /* collect.parallelStream()
                .map(time -> executorService.submit(() ->
                        getModelApiRecordFromMyModelService(tsrRequest.getTerminalId(), tsrRequest.getOrgId(),
                                tsrRequest.getStartTime(), tsrRequest.getEndTime()))).collect(Collectors.toList());
*/
       /* List<Future<List<GpiRecord>>> futureList = myCompanies.parallelStream()
                .filter(myCompany -> myCompany.isActive())
                .map(myCompany -> executorService.submit(() ->
                        getModelApiRecordFromMyModelService(tsrRequest.getTerminalId(), myCompany.getMyOrgId(),
                                tsrRequest.getStartTime(), tsrRequest.getEndTime()))).collect(Collectors.toList());
*/

      /*  List<GpiRecord> collectedGpiRecords = futureList.parallelStream().map(listFuture -> {
            try {
                return listFuture.get();
            } catch (InterruptedException | ExecutionException ex) {
                logger.error("Unable to get gpiRecords for future list: ", ex);
            }
            return new ArrayList<GpiRecord>();
        }).flatMap(List::stream).collect(Collectors.toList());*/

        if (CollectionUtils.isNullOrEmpty(allGpiRecords)) {
            return null;
        }
        pool.shutdown();
        sort(allGpiRecords, Comparator.comparing(GpiRecord::getEventTime));
        return allGpiRecords;
    }

    /*private Function<String, List<GpiRecord>> fetchModelApiRecords(String interval, TSRRequest tsrRequest) throws IOException {
        return interval -> {
            getModelApiRecordFromMyModelService(tsrRequest.getTerminalId(),tsrRequest.getOrgId(),
                    setStartTime(interval,tsrRequest),setEndTIme(interval,tsrRequest));
        }
    }*/

    private String setEndTIme(String interval, TSRRequest tsrRequest) {
        String[] splitedIntervals = interval.split("=");
        String endTime = splitedIntervals[0];
        System.out.println(endTime);
        if(parse(tsrRequest.getEndTime()).isAfter(parse(endTime))){
            return endTime;
        }
        return tsrRequest.getEndTime();
    }

    private String setStartTime(String interval, TSRRequest tsrRequest) {
        String[] splitedTimeIntervals = interval.split("=");
        String startTime = splitedTimeIntervals[1];
        System.out.println(startTime);
        if(parse(startTime).isAfter(parse(tsrRequest.getStartTime()))){
            return startTime;
        }
        return tsrRequest.getStartTime();
    }

    private List<GpiRecord> getModelApiRecordFromMyModelService(Long terminalId, String myOrgId,
                                                                String startTime, String endTime) throws IOException {
        List<GpiRecord> modelGpiRecord = new ArrayList<>();
        String url = buildMyModelServiceURLForTerminalData(terminalId, myOrgId, startTime, endTime);

        System.out.println(url);
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        GpiRecord gpiRecord = new JsonUtility().converCollectionFromJson(forEntity.getBody(), GpiRecord.class);
        modelGpiRecord.add(gpiRecord);

        return modelGpiRecord;
    }

    private String buildMyModelServiceURLForTerminalData(Long terminalId, String myOrgId, String startTime, String endTime) {

        return String.valueOf(new StringBuilder("http://my.vivek.com").append(MY_DATA_API).append(DATA_API)
                .append("?").append("terminalId=").append(terminalId).append(DELIMITER)
                .append("orgId=").append(myOrgId).append(DELIMITER).append("startTime=").append(startTime)
                .append(DELIMITER).append("endTime=").append(endTime));
    }

    private String getMyServiceApi() {
        return PropertyLoader.getPropValues(MY_MODEL_API);
    }

    private static TSRRequest getTsrReqest() {
        TSRRequest tsrRequest = new TSRRequest();
        tsrRequest.setCount(100);
        tsrRequest.setStartIndex(0);
        tsrRequest.setTerminalId(9049L);
        tsrRequest.setContentType("application/json");
        tsrRequest.setOrgId("10000");
        tsrRequest.setStartTime("2018-02-21T05:00:05.201Z");
        tsrRequest.setEndTime("2018-04-01T07:05:05.101Z");
        return tsrRequest;
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ModelService modelService = new ModelService();
        List<String> timeIntervals = Arrays.asList("2018-04-01T02:01:30.610Z=2018-04-01T02:01:30.610Z",
                "2018-03-30T23:25:30.420Z=2018-03-30T11:25:30.420Z", "2018-03-30T11:25:29.420Z=2018-03-30T07:25:30.514Z",
                "2018-03-28T20:25:30.500Z=2018-03-28T15:25:30.561Z", "2018-03-25T23:25:30.111Z=2018-03-25T11:25:30.111Z","2018-03-25T11:25:29.111Z=2018-03-25T09:25:30.120Z",
                "2018-03-24T17:25:30.215Z=2018-03-24T07:25:30.012Z","2018-03-22T22:25:30.333Z=2018-03-22T10:25:30.333Z","2018-03-22T10:25:29.333Z=2018-03-22T05:25:30.610Z",
                "2018-03-21T22:25:30.333Z=2018-03-21T10:25:30.333Z","2018-03-21T10:25:29.333Z=2018-03-21T05:25:30.610Z");
        TSRRequest tsrRequest = getTsrReqest();
        modelService.getModelApiRecords(tsrRequest,timeIntervals);
    }
}
