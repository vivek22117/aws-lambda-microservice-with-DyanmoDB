package com.vivek.tsr.lambda;


import com.vivek.tsr.domain.MyResponse;
import com.vivek.tsr.domain.UserRequest;
import com.vivek.tsr.exception.ApplicationException;
import com.vivek.tsr.service.RequestResponse;
import com.vivek.tsr.utility.ValidateRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.vivek.tsr.utility.AppUtil.convert;
import static com.vivek.tsr.utility.AppUtil.isValidContentType;

/**
 * Created by Vivek Kumar Mishra on 30-01-2018.
 */
public class APIHandler {
    private RequestResponse requestResponse;
    private ValidateRequest validateRequest;

    private static final Logger LOGGER = LogManager.getLogger(APIHandler.class);

    public APIHandler() {
        this(new RequestResponse(), new ValidateRequest());
    }

    public APIHandler(RequestResponse requestResponse, ValidateRequest validateRequest) {
        this.requestResponse = requestResponse;
        this.validateRequest = validateRequest;
    }

    public void processRequest(UserRequest userRequest) {
        LOGGER.error("Process request has terminalId: ", userRequest.getEmployeeId());
        try {
            if (validateRequest.validateRequest(userRequest)) {
                if (isValidContentType(userRequest)) {
                    new MyResponse(convert(userRequest.getContentType(), requestResponse.getDsrResponse(userRequest)),
                            userRequest.getContentType());
                }
                throw new ApplicationException("Content type is not valid: ", null);
            }
            throw new ApplicationException("Bad Reques, please provide valid parameters in request: ", null);
        } catch (Exception e) {
            throw new ApplicationException("Bad Request :", null);
        }
    }
}
