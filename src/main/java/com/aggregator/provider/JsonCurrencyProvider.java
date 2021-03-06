package com.aggregator.provider;

import com.aggregator.model.CurrencyRate;
import com.aggregator.utils.FileUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class JsonCurrencyProvider implements CurrencyProvider {

    private static final Logger log =
            LogManager.getLogger(JsonCurrencyProvider.class);

    public List<CurrencyRate> getData(final File file) {
        ArrayList<CurrencyRate> resultList = new ArrayList<>();
        if (file.length() != 0) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                resultList = mapper
                        .readValue(file,
                                new TypeReference<Collection<CurrencyRate>>() {
                                });
            } catch (IOException e) {
               log.error("Exception while reading from JSON", e);
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
            for (JsonNode node : rootNode) {
                if (node.get("code").asText().equals(code)) {
                    ((ObjectNode) node).put(tag, newValue);
                }
            }
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(file, rootNode);

        } catch (IOException e) {
            log.error("Exception while writing to JSON", e);
        }
    }
}
