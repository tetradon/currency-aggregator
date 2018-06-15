package com.aggregator.controller;

import com.aggregator.model.CurrencyRate;
import com.aggregator.service.CurrencyService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class CurrencyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CurrencyService currencyServiceMock;

    @InjectMocks
    private CurrencyController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();

        Map<String, List<CurrencyRate>> testedMap = new HashMap<>();

        CurrencyRate usdRate = new CurrencyRate("USD", 26.11, 27.11);
        CurrencyRate rubRate = new CurrencyRate("RUB", 0.4, 0.5);
        String bank = "pumb";

        testedMap.put(bank, Arrays.asList(usdRate, rubRate));
        when(currencyServiceMock.getAllRates()).thenReturn(testedMap);

        HashMap<String, CurrencyRate> usdRateMap = new HashMap<>();
        usdRateMap.put(bank, usdRate);

        HashMap<String, CurrencyRate> rubRateMap = new HashMap<>();
        rubRateMap.put(bank, rubRate);

        HashMap<String, Double> rubBuyRatesMap = new HashMap<>();
        rubBuyRatesMap.put(bank, rubRate.getBuy());

        HashMap<String, Double> usdSellRatesMap = new HashMap<>();
        usdSellRatesMap.put(bank, usdRate.getSell());

        Map<String, Map<String, Map.Entry<String, Double>>> report = new HashMap<>();

        Map.Entry<String, Double> bankBuyUSDEntry = new AbstractMap.SimpleEntry<>(bank, usdRate.getBuy());
        Map.Entry<String, Double> bankSellUSDEntry = new AbstractMap.SimpleEntry<>(bank, usdRate.getSell());
        Map<String, Map.Entry<String, Double>> mapUSDEntry = new HashMap<>();
        mapUSDEntry.put("buy", bankBuyUSDEntry);
        mapUSDEntry.put("sell", bankSellUSDEntry);
        report.put("USD", mapUSDEntry);

        Map.Entry<String, Double> bankBuyRUBEntry = new AbstractMap.SimpleEntry<>(bank, rubRate.getBuy());
        Map.Entry<String, Double> bankSellRUBEntry = new AbstractMap.SimpleEntry<>(bank, rubRate.getSell());
        Map<String, Map.Entry<String, Double>> mapRUBEntry = new HashMap<>();
        mapRUBEntry.put("buy", bankBuyRUBEntry);
        mapRUBEntry.put("sell", bankSellRUBEntry);
        report.put("RUB", mapRUBEntry);

        when(currencyServiceMock.getRatesForCode("USD")).thenReturn(usdRateMap);
        when(currencyServiceMock.getRatesForCode("RUB")).thenReturn(rubRateMap);
        when(currencyServiceMock.getBuyPricesForCode("RUB")).thenReturn(rubBuyRatesMap);
        when(currencyServiceMock.getSellPricesForCode("USD")).thenReturn(usdSellRatesMap);
        when(currencyServiceMock.getBestPropositions()).thenReturn(report);

    }

    @Test
    public void testGetAllRates() throws Exception {
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("{\"pumb\":[{\"code\":\"USD\",\"buy\":26.11,\"sell\":27.11},{\"code\":\"RUB\",\"buy\":0.4,\"sell\":0.5}]}",result.getResponse().getContentAsString());
    }

    @Test
    public void testGetAllRatesForUSD() throws Exception {
        MvcResult result = mockMvc.perform(get("/USD"))
                .andExpect(status().isOk())
                .andReturn();
       assertEquals("{\"pumb\":{\"code\":\"USD\",\"buy\":26.11,\"sell\":27.11}}",result.getResponse().getContentAsString());
    }

    @Test
    public void testGetAllRatesForRUB() throws Exception {
        MvcResult result = mockMvc.perform(get("/RUB"))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
        assertEquals("{\"pumb\":{\"code\":\"RUB\",\"buy\":0.4,\"sell\":0.5}}" , result.getResponse().getContentAsString());
    }

    @Test
    public void testGetRUBBuyPrices() throws Exception {
        MvcResult result = mockMvc.perform(get("/RUB/buy"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("{\"pumb\":0.4}",result.getResponse().getContentAsString());
    }

    @Test
    public void testGetUSDSellPrices() throws Exception {
        MvcResult result = mockMvc.perform(get("/USD/sell"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("{\"pumb\":27.11}", result.getResponse().getContentAsString());
    }

    @Test
    public void testUpdateUSDSellPrice() throws Exception {
        MvcResult result = mockMvc.perform(put("/RUB/buy")
                .param("bank","pumb")
                .param("value", "0.01")).andReturn();
        assertEquals("{\"status\" : \"ok\"}", result.getResponse().getContentAsString());
    }

    @Test
    public void testDelete() throws Exception{
        MvcResult result = mockMvc.perform(delete("/")
                .param("bank","pumb"))
                .andReturn();
        assertEquals("{\"status\" : \"ok\"}" ,result.getResponse().getContentAsString());
    }

    @Test
    public void testGetBestProposition() throws Exception {
        MvcResult result = mockMvc.perform(get("/report"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("{\"USD\":{\"buy\":{\"pumb\":26.11},\"sell\":{\"pumb\":27.11}},\"RUB\":{\"buy\":{\"pumb\":0.4},\"sell\":{\"pumb\":0.5}}}" , result.getResponse().getContentAsString());
    }
    @Test
    public void testGetRUBBuyPricesSorted() throws Exception {
        MvcResult result = mockMvc.perform(get("/RUB/buy")
                .param("sort", "asc"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("{\"pumb\":0.4}", result.getResponse().getContentAsString());
    }

}
