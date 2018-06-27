package com.aggregator.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.money.MonetaryAmount;
import java.io.IOException;

public final class MonetaryAmountSerializer extends StdSerializer<MonetaryAmount> {

    public MonetaryAmountSerializer() {
        this(null);
    }

    private MonetaryAmountSerializer(Class<MonetaryAmount> t) {
        super(t);
    }

    @Override
    public void serialize(MonetaryAmount monetaryAmount,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeNumber(monetaryAmount
                .getNumber().doubleValueExact());
    }
}

