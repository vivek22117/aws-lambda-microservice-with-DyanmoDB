package com.vivek.tsr.utility;

import com.amazonaws.util.StringUtils;
import com.vivek.tsr.domain.TSRRequest;

import java.time.Instant;
import java.util.Objects;

import static com.amazonaws.util.StringUtils.*;

/**
 * Created by Vivek Kumar Mishra on 20-03-2018.
 */
public class ValidateRequest {

    public boolean validateRequest(TSRRequest tsrRequest){
        if(tsrRequest.isLastKnown()){
            return validateLastKnownRequestParameters(tsrRequest);
        }

        return (validateTimeInterval(tsrRequest) && validateCount(tsrRequest));
    }

    private boolean validateLastKnownRequestParameters(TSRRequest tsrRequest) {
        return (isNullOrEmpty(tsrRequest.getStartTime()) && isNullOrEmpty(tsrRequest.getEndTime())
                && (Objects.isNull(tsrRequest.getCount())));
    }

    private boolean validateTimeInterval(TSRRequest tsrRequest){
        if(!isNullOrEmpty(tsrRequest.getStartTime()) && !isNullOrEmpty(tsrRequest.getEndTime())){
            if(Instant.parse(tsrRequest.getStartTime()).isAfter(Instant.parse(tsrRequest.getEndTime()))){
                return false;
            }
            return true;
        }
        return true;
    }

    private boolean validateCount(TSRRequest tsrRequest){
        if(tsrRequest.getCount()<=0){
            return false;
        }else if(tsrRequest.getCount() > 250){
            return false;
        }
        return true;
    }
}
