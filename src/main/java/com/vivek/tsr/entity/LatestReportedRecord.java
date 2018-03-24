package com.vivek.tsr.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created by Vivek Kumar Mishra on 24-03-2018.
 */

@DynamoDBTable(tableName = "MyDyanamoDBTable")
public class LatestReportedRecord {

    @DynamoDBHashKey(attributeName = "deviceIdCompanyId")
    private String deviceIdCompanyId;

    @DynamoDBAttribute(attributeName = "employeeId")
    private String employeeId;

    @DynamoDBAttribute(attributeName = "companyId")
    private String companyId;

    @DynamoDBRangeKey(attributeName = "createdDate")
    private String createdDate;

    public String getDeviceIdCompanyId() {
        return deviceIdCompanyId;
    }

    public void setDeviceIdCompanyId(String deviceIdCompanyId) {
        this.deviceIdCompanyId = deviceIdCompanyId;
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
