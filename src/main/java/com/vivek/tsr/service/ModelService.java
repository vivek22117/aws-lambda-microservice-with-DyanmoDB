package com.vivek.tsr.service;

import com.vivek.tsr.domain.ApiRecord;
import com.vivek.tsr.domain.MyCompany;
import com.vivek.tsr.domain.TSRRequest;
import com.vivek.tsr.utility.PropertyLoader;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by HARSHA on 07-02-2018.
 */
public class ModelService {
    private static final String MY_MODEL_API = "myModelApi";
    private static final String MY_DATA_API = "myDataApi";
    private static final String DATA_API = "/getByTypeAndOrgId/";
    private static final String DELIMITER = "&";

    private RestTemplate restTemplate;

    public ModelService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void getModelApiRecords(TSRRequest tsrRequest, List<MyCompany> myCompanies) {

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);

        myCompanies.parallelStream()
                .filter(myCompany -> myCompany.isActive())
                .map(myCompany -> executorService.submit(() ->
                        getModelApiRecordFromMyModelService(tsrRequest.getTerminalId(), myCompany.getMyOrgId(),
                                tsrRequest.getStartTime(), tsrRequest.getEndTime()))).collect(Collectors.toList());


//        Map<Long, List<MyCompany>> collect = filteredMachineCompanies.entrySet().stream()
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    private List<ApiRecord> getModelApiRecordFromMyModelService(Long terminalId, String myOrgId,
                                                                String startTime, String endTime) {
        String url = buildMyModelServiceURLForTerminalData(terminalId, myOrgId, startTime, endTime);

        ApiRecord apiRecord = restTemplate.getForObject(url, ApiRecord.class);

        return new ArrayList<>();
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
