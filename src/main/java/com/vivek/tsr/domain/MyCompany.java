package com.vivek.tsr.domain;

/**
 * Created by HARSHA on 07-02-2018.
 */
public class MyCompany {

    private String id;
    private Long deviceId;
    private String myOrgId;
    private String myCompanyId;
    private String beginTime;
    private String endTime;
    private boolean isActive;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMyOrgId() {
        return myOrgId;
    }

    public void setMyOrgId(String myOrgId) {
        this.myOrgId = myOrgId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getMyCompanyId() {
        return myCompanyId;
    }

    public void setMyCompanyId(String myCompanyId) {
        this.myCompanyId = myCompanyId;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
