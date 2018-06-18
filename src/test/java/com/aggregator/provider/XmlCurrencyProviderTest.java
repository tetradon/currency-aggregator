package com.aggregator.provider;

import com.aggregator.model.CurrencyRate;
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
            assertEquals(rateList.get(i).getCode(), ratesFromFile.get(i).getCode());
            assertEquals(rateList.get(i).getBuy(), ratesFromFile.get(i).getBuy());
            assertEquals(rateList.get(i).getSell(), ratesFromFile.get(i).getSell());
        }
    }

    @Test
    public void testUpdateBuyPrice() {
        Double newValue = 999.999;
        provider.updateBuyPrice(file, "USD", newValue);
        CurrencyRate usdFromFile = provider.getData(file).get(0);
        assertEquals(newValue, usdFromFile.getBuy());
    }

    @Test
    public void testUpdateSellPrice() {
        Double newValue = 999.999;
        provider.updateSellPrice(file, "RUB", newValue);
        CurrencyRate rubFromFile = provider.getData(file).get(1);
        assertEquals(newValue, rubFromFile.getSell());
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
