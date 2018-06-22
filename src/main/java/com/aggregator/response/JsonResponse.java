package com.aggregator.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.Map;

public final class JsonResponse {
    public static final  String OK_RESPONSE = "{\"status\" : \"ok\"}";
    private static ObjectMapper mapper = new ObjectMapper();

    private static final Logger log =
            LogManager.getLogger(JsonResponse.class);

    private JsonResponse() {
    }

    public static String getJsonFromMap(final Map map) {
        String response = "";
        try {
            response = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Exception while JSON processing", e);
        }
        return response;
    }
}
