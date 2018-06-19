package com.aggregator.storage;

import com.aggregator.model.CurrencyRate;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CurrencyStorageTest {
    private Map<String, List<CurrencyRate>> currencyData = new HashMap<>();

    private CurrencyRate pumbUsd;
    private CurrencyRate pumbRub;
    private CurrencyRate pumbEur;

    private String bankPumb = "pumb";

    private CurrencyRatesStorage storage;

    @Before
    public void setup() {
        pumbUsd = new CurrencyRate("USD", 25.85, 26.1);
        pumbRub = new CurrencyRate("RUB", 0.37, 0.43);
        pumbEur = new CurrencyRate("EUR", 30.4, 30.69);
        currencyData.put(bankPumb, Arrays.asList(pumbUsd, pumbRub, pumbEur));

        storage = new CurrencyRatesStorage(currencyData);
    }

    @Test
    public void testUpdateBuyPrice(){
        Double newValue = 123.123;
        storage.updateBuyPriceForBank(bankPumb,"USD",newValue);
        assertEquals(storage.getBuyPricesForCode("USD").get("pumb"),newValue );
    }

    @Test
    public void testUpdateSellPrice(){
        Double newValue = 123.123;
        storage.updateSellPriceForBank(bankPumb,"RUB",newValue);
        assertEquals(storage.getSellPricesForCode("RUB").get("pumb"),newValue );
    }

    @Test
    public void testUpdateNullSellRatesForCode(){
        storage.getAllRates().get(bankPumb).set(0, null);
        storage.updateSellPriceForBank(bankPumb,"USD",999.);
        assertTrue(storage.getSellPricesForCode("USD").isEmpty());
    }

    @Test
    public void testUpdateNullBuyRatesForCode(){
        storage.getAllRates().get(bankPumb).set(1, null);
        storage.updateBuyPriceForBank(bankPumb,"RUB",999.);
        assertTrue(storage.getBuyPricesForCode("RUB").isEmpty());
    }

    @Test
    public void testDelete(){
        storage.deleteRatesForBank("pumb");
        assertEquals(0,storage.getAllRates().size());
    }

    @Test
    public void testGetRatesForCodeWithNull(){
        storage.putData("bank", null);
        Map<String, CurrencyRate> map = new HashMap<>();
        map.put(bankPumb, pumbUsd);
        map.put("bank", null);
        assertEquals(map, storage.getRatesForCode("USD"));
    }

    @Test
    public void testGetRatesForCodeEmptyStorage(){
        storage.getAllRates().clear();
        assertTrue(storage.getRatesForCode("USD").isEmpty());
    }

    @Test
    public void testGetBuyRatesForCodeEmptyStorage(){
        storage.getAllRates().clear();
        assertTrue(storage.getBuyPricesForCode("USD").isEmpty());
    }

    @Test
    public void testGetSellRatesForCodeEmptyStorage(){
        storage.getAllRates().clear();
        assertTrue(storage.getSellPricesForCode("USD").isEmpty());
    }

    @Test
    public void testGetNullSellRatesForCode(){
        storage.getAllRates().get(bankPumb).set(0, null);
        assertTrue(storage.getSellPricesForCode("USD").isEmpty());
    }

    @Test
    public void testGetNullBuyRatesForCode(){
        storage.getAllRates().get(bankPumb).set(1, null);
        assertTrue(storage.getBuyPricesForCode("RUB").isEmpty());
    }

    @Test
    public void testGetBuyPricesForCodeThatForbiddenToBuy(){
        storage.putData("bank", Collections.singletonList(new CurrencyRate("CHF", 0., 123.)));
        Map<String, Double> map = new HashMap<>();
        map.put(bankPumb, pumbRub.getBuy());
        assertEquals(map, storage.getBuyPricesForCode("RUB"));

    }

    @Test
    public void testGetSellPricesForCodeThatForbiddenToSell(){
        storage.putData("bank", Collections.singletonList(new CurrencyRate("CHF", 123., 0.)));
        Map<String, Double> map = new HashMap<>();
        map.put(bankPumb, pumbEur.getSell());
        assertEquals(map, storage.getSellPricesForCode("EUR"));
    }
}
