package com.aggregator.service;


import org.javamoney.moneta.Money;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CurrencyInMemoryServiceMutationTest {
    private File folder;
    private CurrencyInMemoryService service;
    private String bank = "aval";

    @Before
    public void setup() throws IOException {
        folder = Files.createTempDirectory("temp_rates").toFile();

        File file = new File(folder, "aval.xml");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(
                    "<rates>\n" +
                    "    <rate>\n" +
                    "        <code>USD</code>\n" +
                    "        <buy>25.85</buy>\n" +
                    "        <sell>26.1</sell>\n" +
                    "    </rate>\n" +
                    "    <rate>\n" +
                    "        <code>RUB</code>\n" +
                    "        <buy>0.37</buy>\n" +
                    "        <sell>0.43</sell>\n" +
                    "    </rate>\n" +
                    "    <rate>\n" +
                    "        <code>EUR</code>\n" +
                    "        <buy>30.4</buy>\n" +
                    "        <sell>30.69</sell>\n" +
                    "    </rate>\n" +
                    "</rates>");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        service = new CurrencyInMemoryService(folder);
    }


    @Test
    public void testUpdateBuy() {
        Double newValue = 999.;
        service.updateBuyPriceForBank(bank, "USD", newValue.toString());
        assertEquals(Money.of(newValue,"USD"), service.getBuyPricesForCode("USD").get(bank));
    }

    @Test
    public void testUpdateSell() {
        Double newValue = 999.;
        service.updateSellPriceForBank(bank, "RUB", newValue.toString());
        assertEquals(Money.of(newValue,"RUB"), service.getSellPricesForCode("RUB").get(bank));
    }

    @Test
    public void testDelete() {
        service.deleteRatesForBank(bank);
        assertTrue(service.getAllRates().isEmpty());
    }


    @After
    public void cleanup() {
        deleteDirectory(folder);
    }

    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        directory.delete();
    }
}
