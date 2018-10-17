package com.vivek.tsr.utility;

import com.vivek.tsr.domain.UserRequest;

import java.time.Instant;
import java.util.Objects;

import static com.amazonaws.util.StringUtils.*;

/**
 * Created by Vivek Kumar Mishra on 20-03-2018.
 */
public class ValidateRequest {

    public boolean validateRequest(UserRequest userRequest){
        if(userRequest.isLastReporting()){
            return validateLastKnownRequestParameters(userRequest);
        }

        return (validateTimeInterval(userRequest) && validateCount(userRequest));
    }

    private boolean validateLastKnownRequestParameters(UserRequest userRequest) {
        return (isNullOrEmpty(userRequest.getStartTime()) && isNullOrEmpty(userRequest.getEndTime())
                && (Objects.isNull(userRequest.getCount())));
    }

    private boolean validateTimeInterval(UserRequest userRequest){
        if(!isNullOrEmpty(userRequest.getStartTime()) && !isNullOrEmpty(userRequest.getEndTime())){
            if(Instant.parse(userRequest.getStartTime()).isAfter(Instant.parse(userRequest.getEndTime()))){
                return false;
            }
            return true;
        }
        return true;
    }

    private boolean validateCount(UserRequest userRequest){
        if(userRequest.getCount()<=0){
            return false;
        }else if(userRequest.getCount() > 250){
            return false;
        }
        return true;
    }
}
