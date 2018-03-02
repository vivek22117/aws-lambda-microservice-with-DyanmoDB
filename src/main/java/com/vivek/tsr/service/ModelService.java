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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.Collections.sort;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by HARSHA on 07-02-2018.
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

    public List<GpiRecord> getModelApiRecords(TSRRequest tsrRequest, List<MyCompany> myCompanies) {

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);

        List<Future<List<GpiRecord>>> futureList = myCompanies.parallelStream()
                .filter(myCompany -> myCompany.isActive())
                .map(myCompany -> executorService.submit(() ->
                        getModelApiRecordFromMyModelService(tsrRequest.getTerminalId(), myCompany.getMyOrgId(),
                                tsrRequest.getStartTime(), tsrRequest.getEndTime()))).collect(Collectors.toList());


        List<GpiRecord> collectedGpiRecords = futureList.parallelStream().map(listFuture -> {
            try {
                return listFuture.get();
            } catch (InterruptedException | ExecutionException ex) {
                logger.error("Unable to get gpiRecords for future list: ", ex);
            }
            return new ArrayList<GpiRecord>();
        }).flatMap(List::stream).collect(Collectors.toList());

        if (CollectionUtils.isNullOrEmpty(collectedGpiRecords)) {
            return null;
        }
        executorService.shutdown();
        sort(collectedGpiRecords, Comparator.comparing(GpiRecord::getEventTime));
        return collectedGpiRecords;
    }

    private List<GpiRecord> getModelApiRecordFromMyModelService(Long terminalId, String myOrgId,
                                                                String startTime, String endTime) throws IOException {
        List<GpiRecord> modelGpiRecord = new ArrayList<>();
        String url = buildMyModelServiceURLForTerminalData(terminalId, myOrgId, startTime, endTime);

        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        GpiRecord gpiRecord = new JsonUtility().convertFromJson(forEntity.getBody(), GpiRecord.class);
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
}
