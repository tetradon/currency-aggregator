package com.aggregator.provider;

import com.aggregator.model.CurrencyRate;
import org.javamoney.moneta.Money;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class XmlCurrencyProviderTest {

    private File file;
    private CurrencyProvider provider;
    private List<CurrencyRate> rateList;

    @Before
    public void setup() throws IOException {
        CurrencyRate usd = new CurrencyRate("USD", 25.85, 26.1);
        CurrencyRate rub = new CurrencyRate("RUB", 0.405, 0.43);
        CurrencyRate eur = new CurrencyRate("EUR", 30.1, 30.65);
        rateList = Arrays.asList(usd, rub, eur);

        file = File.createTempFile("temp", ".xml");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("<rates>\n" +
                    "    <rate>\n" +
                    "        <code>USD</code>\n" +
                    "        <buy>25.85</buy>\n" +
                    "        <sell>26.1</sell>\n" +
                    "    </rate>\n" +
                    "    <rate>\n" +
                    "        <code>RUB</code>\n" +
                    "        <buy>0.405</buy>\n" +
                    "        <sell>0.43</sell>\n" +
                    "    </rate>\n" +
                    "    <rate>\n" +
                    "        <code>EUR</code>\n" +
                    "        <buy>30.1</buy>\n" +
                    "        <sell>30.65</sell>\n" +
                    "    </rate>\n" +
                    "</rates>");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        provider = new XmlCurrencyProvider();
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
        assertEquals(Money.of(newValue,"USD"), usdFromFile.getCurrencyRateBuyPrice());
    }

    @Test
    public void testUpdateSellPrice() {
        Double newValue = 999.999;
        provider.updateSellPrice(file, "RUB", newValue);
        CurrencyRate rubFromFile = provider.getData(file).get(1);
        assertEquals(Money.of(newValue,"RUB"), rubFromFile.getCurrencyRateSellPrice());
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
