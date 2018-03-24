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





    public List<GpiRecord> getModelApiRecords(TSRRequest tsrRequest, List<String> timeIntervals) throws ExecutionException, InterruptedException {

//        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        List<GpiRecord> allGpiRecords = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(4);

        System.out.println(timeIntervals);

        List<GpiRecord> gpiRecords = pool.submit(() -> timeIntervals.parallelStream().map(interval -> {
            try {
                return getModelApiRecordFromMyModelService(tsrRequest.getTerminalId(), tsrRequest.getOrgId()
                        , setStartTime(interval, tsrRequest), setEndTIme(interval, tsrRequest));
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
        return "";
    }

    private String setStartTime(String interval, TSRRequest tsrRequest) {

        return "";
    }

    private List<GpiRecord> getModelApiRecordFromMyModelService(Long terminalId, String myOrgId,
                                                                String startTime, String endTime) throws IOException {
        List<GpiRecord> modelGpiRecord = new ArrayList<>();
        String url = buildMyModelServiceURLForTerminalData(terminalId, myOrgId, startTime, endTime);

        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        GpiRecord gpiRecord = new JsonUtility().converCollectionFromJson(forEntity.getBody(), GpiRecord.class);
        modelGpiRecord.add(gpiRecord);

        return modelGpiRecord;
    }

    private String buildMyModelServiceURLForTerminalData(Long terminalId, String myOrgId, String startTime, String endTime) {

        return String.valueOf(new StringBuilder(getMyServiceApi()).append(MY_DATA_API).append(DATA_API)
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
        tsrRequest.setStartTime("2018-01-21T01:01:01");
        tsrRequest.setEndTime("2018-03-03T23:59:59");
        return tsrRequest;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ModelService modelService = new ModelService();
        List<String> timeIntervals = Arrays.asList("2018-01-21T00:00:00.000", "2018-01-21T12:00:00.000", "2018-01-20T00:00:00.000", "2018-01-20T12:00:00.000","2018-01-19T00:00:00.000", "2018-01-19T12:00:00.000");
        TSRRequest tsrRequest = getTsrReqest();
        modelService.getModelApiRecords(tsrRequest,timeIntervals);
    }
}
