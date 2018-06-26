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

public class CsvCurrencyProviderTest {
    private File file;
    private CurrencyProvider provider;
    private List<CurrencyRate> rateList;

    @Before
    public void setup() throws IOException {
        CurrencyRate usd = new CurrencyRate("USD", 25.85, 26.1);
        CurrencyRate chf = new CurrencyRate("RUB", 0.37, 0.43);
        CurrencyRate eur = new CurrencyRate("EUR", 30.4, 30.69);
        rateList = Arrays.asList(usd, chf, eur);

        file = File.createTempFile("temp", ".csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(
                        "USD,25.85,26.1\n" +
                            "RUB,0.37,0.43\n" +
                            "EUR,30.4,30.69"
            );
            writer.flush();
        }
        provider = new CsvCurrencyProvider();
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
        int indexOfUpdatedValue = provider.getData(file).size() - 1;
        CurrencyRate usdFromFile = provider.getData(file).get(indexOfUpdatedValue);
        assertEquals(Money.of(newValue, "USD"), usdFromFile.getCurrencyRateBuyPrice());
    }

    @Test
    public void testUpdateSellPrice() {
        Double newValue = 999.999;
        provider.updateSellPrice(file, "RUB", newValue);
        int indexOfUpdatedValue = provider.getData(file).size() - 1;
        CurrencyRate rubFromFile = provider.getData(file).get(indexOfUpdatedValue);
        assertEquals(Money.of(newValue, "RUB"), rubFromFile.getCurrencyRateSellPrice());
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


