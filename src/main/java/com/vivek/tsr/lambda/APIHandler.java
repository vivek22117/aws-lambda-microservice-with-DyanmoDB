package com.vivek.tsr.lambda;

import com.vivek.tsr.domain.RSVPResponse;
import com.vivek.tsr.domain.RSVPRequest;
import com.vivek.tsr.exception.ApplicationException;
import com.vivek.tsr.service.APIResponse;
import com.vivek.tsr.utility.ValidateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.vivek.tsr.utility.AppUtil.convert;
import static com.vivek.tsr.utility.AppUtil.isValidContentType;

public class APIHandler {
    private APIResponse apiResponse;
    private ValidateRequest validateRequest;

    private static final Logger LOGGER = LoggerFactory.getLogger(APIHandler.class);

    public APIHandler() {
        this(new APIResponse(), new ValidateRequest());
    }

    public APIHandler(APIResponse apiResponse, ValidateRequest validateRequest) {
        this.apiResponse = apiResponse;
        this.validateRequest = validateRequest;
    }

    public void processRequest(RSVPRequest rsvpRequest) {
        LOGGER.error("Process request has rsvpId: {} ", rsvpRequest.getRsvp_id());

        try {
            if (validateRequest.validateRequest(rsvpRequest)) {
                if (isValidContentType(rsvpRequest)) {
                    new RSVPResponse(convert(rsvpRequest.getContentType(), apiResponse.gerRSVPResponse(rsvpRequest)),
                            rsvpRequest.getContentType());
                }
                throw new ApplicationException("Content type is not valid: ", null);
            }
            throw new ApplicationException("Bad Request, please provide valid parameters in request: ", null);
        } catch (Exception e) {
            throw new ApplicationException("Bad Request :", null);
        }
    }
}
