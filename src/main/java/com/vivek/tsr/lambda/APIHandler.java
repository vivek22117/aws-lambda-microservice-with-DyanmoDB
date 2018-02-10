package com.vivek.tsr.lambda;


import com.vivek.tsr.domain.TSRRequest;
import com.vivek.tsr.service.DsrResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by HARSHA on 30-01-2018.
 */
public class APIHandler {
    private DsrResponse dsrResponse;

    private static final Logger LOGGER = LogManager.getLogger(APIHandler.class);

    public APIHandler() {
    }

    public void processRequest(TSRRequest tsrRequest){
        LOGGER.error("Process request has terminalId: " , tsrRequest.getTerminalId());
            dsrResponse.getDsrResponse(tsrRequest);
    }
}
