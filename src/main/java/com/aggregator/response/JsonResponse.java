package com.aggregator.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.Map;

public final class JsonResponse {
    public static final  String OK_RESPONSE = "{\"status\" : \"ok\"}";
    private static ObjectMapper mapper = new ObjectMapper();

    private JsonResponse() {
    }

    public static String getJsonFromMap(final Map map) {
        String response = "";
        try {
            response = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response;
    }
}
