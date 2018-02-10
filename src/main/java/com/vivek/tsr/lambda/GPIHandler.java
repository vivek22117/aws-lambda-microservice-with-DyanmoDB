package com.vivek.tsr.lambda;


import com.vivek.tsr.domain.TSRRequest;
import com.vivek.tsr.service.TsrResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by HARSHA on 30-01-2018.
 */
public class GPIHandler {
    private TsrResponse tsrResponse;

    private static final Logger LOGGER = LogManager.getLogger(GPIHandler.class);

    public GPIHandler() {
    }

    public void processRequest(TSRRequest tsrRequest){
        LOGGER.error("Process request has terminalId: " , tsrRequest.getTerminalId());
            tsrResponse.getDsrResponse(tsrRequest);
    }
}
