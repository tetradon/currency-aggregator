package com.aggregator.service;

import com.aggregator.model.CurrencyRate;
import com.aggregator.storage.CurrencyRatesStorage;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class CurrencyInMemoryServiceTest {

    private Map<String, List<CurrencyRate>> currencyData = new HashMap<>();
    private CurrencyInMemoryService service;

    private CurrencyRate pumbUsd;
    private CurrencyRate pumbRub;
    private CurrencyRate pumbEur;

    private CurrencyRate avalUsd;
    private CurrencyRate avalRub;
    private CurrencyRate avalEur;

    private String bankPumb = "pumb";
    private String bankAval = "aval";


    @Before
    public void setup() {
        pumbUsd = new CurrencyRate("USD", 25.85, 26.1);
        pumbRub = new CurrencyRate("RUB", 0.37, 0.43);
        pumbEur = new CurrencyRate("EUR", 30.4, 30.69);
        currencyData.put(bankPumb, Arrays.asList(pumbUsd, pumbRub, pumbEur));

        avalUsd = new CurrencyRate("USD", 25.9, 26.2);
        avalRub = new CurrencyRate("RUB", 0.35, 0.46);
        avalEur = new CurrencyRate("EUR", 30.5, 30.62);
        currencyData.put(bankAval, Arrays.asList(avalUsd, avalRub, avalEur));

        service = new CurrencyInMemoryService(new CurrencyRatesStorage(currencyData));
    }

    @Test
    public void testGetAllRates() {
        assertEquals(currencyData, service.getAllRates());
    }

    @Test
    public void testGetRatesForRUB() {
        Map<String, CurrencyRate> rubMap = new HashMap<>();
        rubMap.put(bankPumb, pumbRub);
        rubMap.put(bankAval, avalRub);
        assertEquals(rubMap, service.getRatesForCode("RUB"));
    }

    @Test
    public void testGetBuyPricesForEur() {
        Map<String, Double> eurMap = new HashMap<>();
        eurMap.put(bankPumb, pumbEur.getCurrencyRateBuyPrice());
        eurMap.put(bankAval, avalEur.getCurrencyRateBuyPrice());
        assertEquals(eurMap, service.getBuyPricesForCode("EUR"));
    }

    @Test
    public void testGetSellPricesForUSD() {
        Map<String, Double> usdMap = new HashMap<>();
        usdMap.put(bankPumb, pumbUsd.getCurrencyRateSellPrice());
        usdMap.put(bankAval, avalUsd.getCurrencyRateSellPrice());
        assertEquals(usdMap, service.getSellPricesForCode("USD"));
    }

    @Test
    public void testGetBestPropositions() {
        Map<String, Map<String, Map.Entry<String, Double>>> report = new HashMap<>();

        Map.Entry<String, Double> buyUSDEntry = new AbstractMap.SimpleEntry<>(bankAval, avalUsd.getCurrencyRateBuyPrice());
        Map.Entry<String, Double> sellUSDEntry = new AbstractMap.SimpleEntry<>(bankPumb, pumbUsd.getCurrencyRateSellPrice());
        Map<String, Map.Entry<String, Double>> mapUSDEntry = new HashMap<>();
        mapUSDEntry.put("buy", buyUSDEntry);
        mapUSDEntry.put("sell", sellUSDEntry);
        report.put("USD", mapUSDEntry);

        Map.Entry<String, Double> buyRUBEntry = new AbstractMap.SimpleEntry<>(bankPumb, pumbRub.getCurrencyRateBuyPrice());
        Map.Entry<String, Double> sellRUBEntry = new AbstractMap.SimpleEntry<>(bankPumb, pumbRub.getCurrencyRateSellPrice());
        Map<String, Map.Entry<String, Double>> mapRUBEntry = new HashMap<>();
        mapRUBEntry.put("buy", buyRUBEntry);
        mapRUBEntry.put("sell", sellRUBEntry);
        report.put("RUB", mapRUBEntry);

        Map.Entry<String, Double> buyEUREntry = new AbstractMap.SimpleEntry<>(bankAval, avalEur.getCurrencyRateBuyPrice());
        Map.Entry<String, Double> sellEUREntry = new AbstractMap.SimpleEntry<>(bankAval, avalEur.getCurrencyRateSellPrice());
        Map<String, Map.Entry<String, Double>> mapEUREntry = new HashMap<>();
        mapEUREntry.put("buy", buyEUREntry);
        mapEUREntry.put("sell", sellEUREntry);
        report.put("EUR", mapEUREntry);
        assertEquals(report, service.getBestPropositions());
    }
}


