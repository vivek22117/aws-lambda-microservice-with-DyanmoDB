package com.vivek.tsr.utility;

import com.amazonaws.util.StringUtils;
import com.vivek.tsr.domain.RSVPRequest;

import java.time.Instant;
import java.util.Objects;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;

public class ValidateRequest {

    public boolean validateRequest(RSVPRequest rsvpRequest) {
        boolean requestIdValid = isRequestIdValid(rsvpRequest);
        if (requestIdValid) {
            if (rsvpRequest.isLastReporting()) {
                return validateLastKnownRequestParameters(rsvpRequest);
            }
            return (validateTimeInterval(rsvpRequest) && validateCount(rsvpRequest));
        }
        return false;
    }

    private boolean validateLastKnownRequestParameters(RSVPRequest rsvpRequest) {
        return (isNullOrEmpty(rsvpRequest.getStartTime()) && isNullOrEmpty(rsvpRequest.getEndTime())
                && (Objects.isNull(rsvpRequest.getCount())));
    }

    private boolean validateTimeInterval(RSVPRequest rsvpRequest) {
        if (!isNullOrEmpty(rsvpRequest.getStartTime()) && !isNullOrEmpty(rsvpRequest.getEndTime())) {
            return !Instant.parse(rsvpRequest.getStartTime()).isAfter(Instant.parse(rsvpRequest.getEndTime()));
        }
        return true;
    }

    private boolean validateCount(RSVPRequest rsvpRequest) {
        if (rsvpRequest.getCount() <= 0) {
            return false;
        }
        return rsvpRequest.getCount() <= 22;
    }

    private boolean isRequestIdValid(RSVPRequest rsvpRequest) {
        if (rsvpRequest.getRsvp_id() == null) {
            if (StringUtils.isNullOrEmpty(rsvpRequest.getEvent_id())) {
                return !StringUtils.isNullOrEmpty(rsvpRequest.getVenue_id());
            }
        }
        return true;
    }
}
