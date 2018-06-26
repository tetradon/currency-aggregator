package com.aggregator.provider;

import com.aggregator.model.CurrencyRate;
import org.javamoney.moneta.Money;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JsonCurrencyProviderTest {
    private File file;
    private CurrencyProvider provider;
    private List<CurrencyRate> rateList;

    @Before
    public void setup() throws IOException {
        CurrencyRate usd = new CurrencyRate("USD", 25.9, 26.1);
        CurrencyRate chf = new CurrencyRate("CHF", 25.75, 26.35);
        CurrencyRate eur = new CurrencyRate("EUR", 30.2, 30.75);
        rateList = Arrays.asList(usd, chf, eur);

        file = File.createTempFile("temp", ".json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("[\n" +
                    "  {\n" +
                    "    \"code\": \"USD\",\n" +
                    "    \"buy\": 25.9,\n" +
                    "    \"sell\": 26.1\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"code\": \"CHF\",\n" +
                    "    \"buy\": 25.75,\n" +
                    "    \"sell\": 26.35\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"code\": \"EUR\",\n" +
                    "    \"buy\": 30.2,\n" +
                    "    \"sell\": 30.75\n" +
                    "  }\n" +
                    "]\n");
            writer.flush();
        }
        provider = new JsonCurrencyProvider();
    }

    @Test
    public void testGetAllRates() {
        List<CurrencyRate> ratesFromFile = provider.getData(file);
        for (int i = 0; i < rateList.size(); i++) {
            assertEquals(rateList.get(i).getCurrencyRateCode(), ratesFromFile.get(i).getCurrencyRateCode());
            assertEquals(rateList.get(i).getCurrencyRateBuyPrice(), ratesFromFile.get(i).getCurrencyRateBuyPrice());
            assertEquals(rateList.get(i).getCurrencyRateSellPrice(), ratesFromFile.get(i).getCurrencyRateSellPrice());
        }
    }

    @Test
    public void testUpdateBuyPrice() {
        Double newValue = 999.999;
        provider.updateBuyPrice(file, "USD", newValue);
        CurrencyRate usdFromFile = provider.getData(file).get(0);
        assertEquals(Money.of(newValue, "USD"), usdFromFile.getCurrencyRateBuyPrice());
    }

    @Test
    public void testUpdateSellPrice() {
        Double newValue = 999.999;
        provider.updateSellPrice(file, "CHF", newValue);
        CurrencyRate chfFromFile = provider.getData(file).get(1);
        assertEquals(Money.of(newValue, "CHF"), chfFromFile.getCurrencyRateSellPrice());
    }

    @Test
    public void testDelete() {
        provider.deleteRatesForBank(file);
        assertEquals(0, file.length());
    }

    @After
    public void cleanup() {
        file.delete();
    }
}
