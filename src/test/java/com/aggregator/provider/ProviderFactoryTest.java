package com.aggregator.provider;


import org.junit.Test;


import static org.junit.Assert.assertTrue;

public class ProviderFactoryTest {
    @Test
    public void testGetXmlProvider(){
        assertTrue(ProviderFactory.getProvider("xml") instanceof XmlCurrencyProvider);
    }
    @Test
    public void testGetJsonProvider(){
        assertTrue(ProviderFactory.getProvider("json") instanceof JsonCurrencyProvider);
    }
    @Test
    public void testGetCsvProvider(){
        assertTrue(ProviderFactory.getProvider("csv") instanceof CsvCurrencyProvider);
    }
}
