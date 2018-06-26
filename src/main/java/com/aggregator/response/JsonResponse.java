package com.aggregator.response;

import com.aggregator.utils.MonetaryAmountSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.money.MonetaryAmount;
import java.util.Map;

public final class JsonResponse {
    public static final  String OK_RESPONSE = "{\"status\" : \"ok\"}";
    private static ObjectMapper mapper = new ObjectMapper();

    private static final Logger log =
            LogManager.getLogger(JsonResponse.class);

    private JsonResponse() {
    }

    public static String getJsonFromMap(final Map map) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(MonetaryAmount.class,
                new MonetaryAmountSerializer());
        mapper.registerModule(module);

        String response = "";
        try {
            response = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Exception while JSON processing", e);
        }
        return response;
    }
}
