package com.vivek.tsr.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.List;

/**
 * Created by Vivek Kumar Mishra on 24-03-2018.
 */

@DynamoDBTable(tableName = "MyDyanamoDBTable")
public class LatestEmployeeRecord {

    @DynamoDBHashKey(attributeName = "empIdCompanyId")
    private String empIdCompanyId;

    @DynamoDBAttribute(attributeName = "employeeId")
    private String employeeId;

    @DynamoDBAttribute(attributeName = "companyId")
    private String companyId;

    @DynamoDBRangeKey(attributeName = "createdDate")
    private String createdDate;

    @DynamoDBAttribute(attributeName = "timeIntervals")
    private List<String> timeIntervals;

    public List<String> getTimeIntervals() {
        return timeIntervals;
    }

    public void setTimeIntervals(List<String> timeIntervals) {
        this.timeIntervals = timeIntervals;
    }

    public String getEmpIdCompanyId() {
        return empIdCompanyId;
    }

    public void setEmpIdCompanyId(String empIdCompanyId) {
        this.empIdCompanyId = empIdCompanyId;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
