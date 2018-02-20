package com.vivek.tsr;

import com.vivek.tsr.domain.GpiRecord;
import com.vivek.tsr.service.DomainService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HARSHA on 20-02-2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDomainService {

    private DomainService domainService;


    @Before
    public void setUp(){
        domainService = new DomainService();
    }

    @Test
    public void shouldProcessAndPersistGPIRecords(){

        domainService.processRecords(new ArrayList<>());
    }
}
