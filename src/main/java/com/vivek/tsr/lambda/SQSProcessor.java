package com.vivek.tsr.lambda;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.vivek.tsr.utility.JsonUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by Vivek Kumar Mishra on 11/11/2018.
 */
public class SQSProcessor {

    private Logger logger = LoggerFactory.getLogger(SQSProcessor.class);
    private JsonUtility jsonUtility;

    public SQSProcessor() {
        this(new JsonUtility());
    }

    public SQSProcessor(JsonUtility jsonUtility) {
        this.jsonUtility = jsonUtility;
    }

    public void processSQSRequest(SQSEvent sqsEvent) {
        sqsEvent.getRecords().forEach(new Consumer<SQSEvent.SQSMessage>() {
            @Override
            public void accept(SQSEvent.SQSMessage sqsMessage) {
                logger.info("Message Id is.." + sqsMessage.getMessageId());
                process(sqsMessage.getBody());
            }
        });
    }

    private String process(String data) {
        try {
            return jsonUtility.convertToJson(data);
        } catch (IOException e) {
            logger.error("Unable to process string to sqs message :", e);
        }
        return null;
    }
}
