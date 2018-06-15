package com.aggregator.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.Map;

public class JsonResponse {
    public final static String okResponse = "{\"status\" : \"ok\"}";
    private static ObjectMapper mapper = new ObjectMapper();

    public static String getJsonFromMap(Map map) {
        String response = "";
        try {
            response = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response;
    }


}
