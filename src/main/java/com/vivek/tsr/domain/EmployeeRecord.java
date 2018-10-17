package com.vivek.tsr.domain;

/**
 * Created by HARSHA on 05-02-2018.
 */
public class EmployeeRecord {
    private String employeeId;
    private String companyId;
    private String eventTime;
    private String empName;
    private String empAddress;
    private String empDesignation;
    private String jobStartDate;
    private String jobEndDate;


    public EmployeeRecord() {
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpAddress() {
        return empAddress;
    }

    public void setEmpAddress(String empAddress) {
        this.empAddress = empAddress;
    }

    public String getEmpDesignation() {
        return empDesignation;
    }

    public void setEmpDesignation(String empDesignation) {
        this.empDesignation = empDesignation;
    }

    public String getJobStartDate() {
        return jobStartDate;
    }

    public void setJobStartDate(String jobStartDate) {
        this.jobStartDate = jobStartDate;
    }

    public String getJobEndDate() {
        return jobEndDate;
    }

    public void setJobEndDate(String jobEndDate) {
        this.jobEndDate = jobEndDate;
    }

    @Override
    public String toString() {
        return "EmployeeRecord{" +
                "employeeId='" + employeeId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", empName='" + empName + '\'' +
                ", empAddress='" + empAddress + '\'' +
                ", empDesignation='" + empDesignation + '\'' +
                ", jobStartDate='" + jobStartDate + '\'' +
                ", jobEndDate='" + jobEndDate + '\'' +
                '}';
    }
}
