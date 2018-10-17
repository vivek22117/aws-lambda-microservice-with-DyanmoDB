package com.vivek.tsr.domain;

import jdk.nashorn.internal.ir.IdentNode;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by HARSHA on 30-01-2018.
 */
public class UserRequest {
    private Integer count;
    private String startTime;
    private String endTime;
    private String contentType;
    private Integer startIndex;
    private Long employeeId;
    private Long companyId;

    private boolean isLastReporting;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public boolean isLastReporting() {
        return isLastReporting;
    }

    public void setLastReporting(boolean lastReporting) {
        isLastReporting = lastReporting;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void initialize() {
        if (isLastReporting) {
            count = 1;
        }
        if (count == 0 && count != null) {
            count = 50;
        }
        if (startIndex == null) {
            startIndex = 0;
        }
        if (endTime == null) {
        }
        if (startTime == null) {
            startTime = Instant.parse(endTime).minus(100, ChronoUnit.MINUTES).toString();
        }

    }

    public static void init(String lastTime) {

    }
}
