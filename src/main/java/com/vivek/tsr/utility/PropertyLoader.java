package com.vivek.tsr.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {
    String result = "";

    public static String getPropValues(String propertyKey){

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = loader.getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            // get the property value and print it out
            String propertyValue = prop.getProperty("propertyKey");
            return propertyValue;
        } catch (Exception e) {
            return null;
        }
    }
}
