package com.vivek.tsr.service;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.vivek.tsr.domain.ApiRecord;

        import java.io.IOException;

/**
 * Created by HARSHA on 05-02-2018.
 */
public class JsonUtility {

    private ObjectMapper mapper = new ObjectMapper();

    public ApiRecord convertToObject(byte[] data) {
        try {
            return mapper.readValue(data, ApiRecord.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
