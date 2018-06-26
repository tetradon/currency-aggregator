package com.aggregator.utils;

import com.aggregator.model.CurrencyRate;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


import java.io.IOException;

public final class CurrencyRateDeserializer
        extends StdDeserializer<CurrencyRate> {

    public CurrencyRateDeserializer() {
        this(null);
    }

    public CurrencyRateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CurrencyRate deserialize(JsonParser jp,
                                    DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String code = node.get("code").asText();
        String buy = node.get("buy").asText();
        String sell = node.get("sell").asText();

        return new CurrencyRate(code,
                Double.parseDouble(buy),
                Double.parseDouble(sell));
    }
}
