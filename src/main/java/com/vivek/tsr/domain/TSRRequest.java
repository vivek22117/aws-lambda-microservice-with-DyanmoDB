package com.vivek.tsr.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by HARSHA on 30-01-2018.
 */
public class TSRRequest {
   private int count;
   private String startTime;
   private String endTime;
   private Integer startIndex;
   private Long terminalId;
   private boolean lastKnown;

   public void initialize(String lastKnownTime) {
       if(lastKnown) {
           count = 1;
       }
       if(count == 0) {
           count = 250;
       }
       if(startIndex == null) {
           startIndex = 0;
       }
       if(endTime == null){
           endTime = lastKnownTime;
       }
       if(startTime == null) {
           startTime = Instant.parse(endTime).minus(100, ChronoUnit.MINUTES).toString();
       }

   }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
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

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }
}
