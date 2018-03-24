package com.vivek.tsr.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vivek.tsr.domain.GpiRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by HARSHA on 01-02-2018.
 */
public class JsonUtility {

    private static Logger logger = LogManager.getLogger(JsonUtility.class);
    private ObjectMapper objectMapper;

    public JsonUtility() {
        this((new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
    }

    public JsonUtility(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> String convertToJson(T object) throws JsonProcessingException {
        try{
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException jx) {
            logger.error("Unable to convert object into Json string: ", jx);
        }
        return null;
    }

    public <T> T convertFromJson(String json, Class<T> object) throws IOException {
        try{
             return objectMapper.readValue(json, object);
        } catch (JsonProcessingException jx){
            logger.error("Unable to conver json string to object: ", jx);
        }
        return null;
    }

    public <T> T converCollectionFromJson(String json, Class<T> object){

        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        try {
            return objectMapper.readValue(json, typeFactory.constructCollectionType(List.class, GpiRecord.class));
        } catch (IOException e) {
            logger.error("Unable to conver json string to list of object: ", e);
        }
        return null;
    }
}
