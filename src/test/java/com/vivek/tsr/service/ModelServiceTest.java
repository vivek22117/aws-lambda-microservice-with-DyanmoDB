package com.vivek.tsr.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.domain.TSRRequest;
import com.vivek.tsr.utility.JsonUtility;
import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.jws.WebParam;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.eq;

/**
 * Created by Vivek Kumar Mishra on 22-03-2018.
 */

@RunWith(MockitoJUnitRunner.class)
public class ModelServiceTest {

    private ModelService modelService;


    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<String> urlCaptor;


    @Before
    public void setUp(){
        modelService = new ModelService(restTemplate, new JsonUtility(), LogManager.getLogger(ModelService.class));
    }

    @Test
    public void shouldGetModelApiRecords() throws ExecutionException, InterruptedException, IOException {

        /*ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        ResponseEntity responseEntity = objectMapper.readValue("{\n" +
                "  \"deviceId\":9049,\n" +
                "  \"machineId\":555,\n" +
                "  \"orgId\":\"10000\",\n" +
                "  \"ancestor\":\"vivek\",\n" +
                "  \"contentType\":\"application/json\",\n" +
                "  \"content\":\"data\",\n" +
                "  \"eventTime\":\"2048-02-22\"\n" +
                "}", ResponseEntity.class);*/
        List<String> reportedDates = Arrays.asList("2018/01/24", "2018/01/22", "2018/02/03");
       /* List<String> timeIntervals = reportedDates.stream().map(data -> getFormattedDate(data)).map(d1 -> getNumberOfIntervals(d1))
                .flatMap(x -> x.stream()).collect(toList());*/

        List<String> timeIntervals = Arrays.asList("2018-01-21T00:00:00.000", "2018-01-21T12:00:00.000", "2018-01-20T00:00:00.000", "2018-01-20T12:00:00.000","2018-01-19T00:00:00.000", "2018-01-19T12:00:00.000");
        TSRRequest tsrRequest = getTsrReqest();


        Mockito.when(restTemplate.getForEntity(urlCaptor.capture(),eq(String.class)))
                .thenReturn(new ResponseEntity<String>(HttpStatus.OK))
                .thenReturn(new ResponseEntity<String>(HttpStatus.OK))
                .thenReturn(new ResponseEntity<String>(HttpStatus.OK))
                .thenReturn(new ResponseEntity<String>(HttpStatus.OK))
                .thenReturn(new ResponseEntity<String>(HttpStatus.OK))
                .thenReturn(new ResponseEntity<String>(HttpStatus.OK))
                .thenReturn(new ResponseEntity<String>(HttpStatus.OK));

        List<GpiRecord> modelApiRecords = modelService.getModelApiRecords(tsrRequest, timeIntervals);

        Assert.assertThat(modelApiRecords.size(),is(""));
    }

    private static String getFormattedDate(String date) {
        Date parse = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            parse = dateFormat.parse(date);
            System.out.println(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ss");
        String formattedDate = formatter.format(parse);
        System.out.println(formattedDate);
        return formattedDate;
    }

    private static List<String> getNumberOfIntervals(String timeProvided) {
        String startTime = String.valueOf(Instant.parse(timeProvided).truncatedTo(ChronoUnit.SECONDS));
        String firstInterval = String.valueOf(Instant.parse(timeProvided).plus(43200, ChronoUnit.SECONDS));
        return Arrays.asList(startTime, firstInterval);
    }

    private TSRRequest getTsrReqest() {
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
}
