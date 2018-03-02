package com.vivek.tsr.service;

import com.vivek.tsr.domain.GpiRecord;
import sun.security.timestamp.TSRequest;

/**
 * Created by HARSHA on 28-02-2018.
 */
public interface GPILastDataService {


    default GpiRecord getLastDataOfDynamoDB(TSRequest tsRequest){

     return null;
    }
}
