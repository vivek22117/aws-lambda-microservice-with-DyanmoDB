package com.vivek.tsr.lambda;


import com.vivek.tsr.domain.MyResponse;
import com.vivek.tsr.domain.TSRRequest;
import com.vivek.tsr.exception.ApplicationException;
import com.vivek.tsr.service.TsrResponse;
import com.vivek.tsr.utility.ValidateRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.vivek.tsr.utility.AppUtil.convert;
import static com.vivek.tsr.utility.AppUtil.isValidContentType;

/**
 * Created by Vivek Kumar Mishra on 30-01-2018.
 */
public class GPIHandler {
    private TsrResponse tsrResponse;
    private ValidateRequest validateRequest;

    private static final Logger LOGGER = LogManager.getLogger(GPIHandler.class);

    public GPIHandler() {
        this(new TsrResponse(), new ValidateRequest());
    }

    public GPIHandler(TsrResponse tsrResponse, ValidateRequest validateRequest) {
        this.tsrResponse = tsrResponse;
        this.validateRequest = validateRequest;
    }

    public void processRequest(TSRRequest tsrRequest) {
        LOGGER.error("Process request has terminalId: ", tsrRequest.getTerminalId());
        try {
            if (validateRequest.validateRequest(tsrRequest)) {
                if (isValidContentType(tsrRequest)) {
                    new MyResponse(convert(tsrRequest.getContentType(), tsrResponse.getDsrResponse(tsrRequest)),
                            tsrRequest.getContentType());
                }
                throw new ApplicationException("Content type is not valid: ", null);
            }
            throw new ApplicationException("Bad Reques, please provide valid parameters in request: ", null);
        } catch (Exception e) {
            throw new ApplicationException("Bad Request :", null);
        }
    }
}
