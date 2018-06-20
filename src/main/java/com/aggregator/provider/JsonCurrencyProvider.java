package com.aggregator.provider;

import com.aggregator.model.CurrencyRate;
import com.aggregator.utils.FileUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class JsonCurrencyProvider implements CurrencyProvider {

    public List<CurrencyRate> getData(final File file) {
        ArrayList<CurrencyRate> resultList = new ArrayList<>();
        if (file.length() != 0) {
            ObjectMapper mapper = new ObjectMapper();
            mapper
                    .configure(DeserializationFeature
                            .ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            try {
                resultList = mapper
                        .readValue(file,
                                new TypeReference<Collection<CurrencyRate>>() {
                                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public void updateBuyPrice(final File file, final String code,
                               final Double newValue) {
        updatePrice(file, code, newValue, "buy");
    }

    public void updateSellPrice(final File file, final String code,
                                final Double newValue) {
        updatePrice(file, code, newValue, "sell");
    }


    @Override
    public void deleteRatesForBank(final File file) {
        FileUtils.deleteContentOfFile(file);
    }

    private void updatePrice(final File file, final String code,
                             final Double newValue, final String tag) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(file);
            Iterator iterator = rootNode.iterator();
            while (iterator.hasNext()) {
                JsonNode node = (JsonNode) iterator.next();
                if (node.get("code").asText().equals(code)) {
                    ((ObjectNode) node).put(tag, newValue);
                }
            }
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(file, rootNode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
