package com.aggregator.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CurrencyRateTest {

    @Test
    public void testNotEquals(){
        CurrencyRate rate1 = new CurrencyRate("USD",12.,12.);
        CurrencyRate rate2 = new CurrencyRate("EUR",12.,12.);
        assertNotEquals(rate1,rate2);
    }

    @Test
    public void testEquals(){
        CurrencyRate rate1 = new CurrencyRate("EUR",12.,12.);
        CurrencyRate rate2 = new CurrencyRate("EUR",12.,12.);
        assertEquals(rate1,rate2);
    }

    @Test
    public void testNotEqualsNull(){
        CurrencyRate rate1 = new CurrencyRate("EUR",12.,12.);
        assertNotEquals(rate1,null);
    }
}
