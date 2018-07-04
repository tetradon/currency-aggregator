package integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestAppConfig.class})
@WebAppConfiguration
public class CurrencyAggregatorIntegrationReadOnlyTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .build();
    }

    @Test
    public void verifyTestConfiguration() {
        ServletContext servletContext = context.getServletContext();
        assertNotNull(servletContext);
        assertNotNull(context.getBean("currencyController"));
    }

    @Test
    public void testGetAllRates() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=ISO-8859-1"))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.otp.*.code", containsInAnyOrder("USD", "CHF", "EUR")))
                .andExpect(jsonPath("$.aval.*.code", containsInAnyOrder("USD", "CHF", "EUR", "RUB", "GBP")))
                .andExpect(jsonPath("$.pumb.*.code", containsInAnyOrder("USD", "CHF", "EUR", "RUB", "GBP")));
    }

    @Test
    public void testGetAllRatesForUsd() throws Exception {
        mockMvc.perform(get("/USD"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=ISO-8859-1"))
                .andExpect(jsonPath("$.otp.code").value("USD"))
                .andExpect(jsonPath("$.otp.buy").value(25.9))
                .andExpect(jsonPath("$.otp.sell").value(26.1))
                .andExpect(jsonPath("$.aval.code").value("USD"))
                .andExpect(jsonPath("$.aval.buy").value(25.9))
                .andExpect(jsonPath("$.aval.sell").value(26.2))
                .andExpect(jsonPath("$.pumb.code").value("USD"))
                .andExpect(jsonPath("$.pumb.buy").value(26.0))
                .andExpect(jsonPath("$.pumb.sell").value(26.2));
    }

    @Test
    public void testGetAllRatesForRub() throws Exception {
        mockMvc.perform(get("/RUB"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=ISO-8859-1"))
                .andExpect(jsonPath("$.pumb.code").value("RUB"))
                .andExpect(jsonPath("$.pumb.buy").value(0.41))
                .andExpect(jsonPath("$.pumb.sell").value(0.42))
                .andExpect(jsonPath("$.aval.code").value("RUB"))
                .andExpect(jsonPath("$.aval.buy").value(0.414))
                .andExpect(jsonPath("$.aval.sell").value(0.424));
    }

    @Test
    public void testGetAllRatesForWrongCurrency() throws Exception {
        MvcResult result = mockMvc.perform(get("/WRONG_CURRENCY"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=ISO-8859-1"))
                .andReturn();
        assertEquals("{ }", result.getResponse().getContentAsString());

    }

    @Test
    public void testGetBuyPricesForGbp() throws Exception {
        mockMvc.perform(get("/GBP/buy"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=ISO-8859-1"))
                .andExpect(jsonPath("$.aval").value(34.4))
                .andExpect(jsonPath("$.pumb").value(34.3));
    }

    @Test
    public void testGetSellPricesForRub() throws Exception {
        mockMvc.perform(get("/RUB/sell"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=ISO-8859-1"))
                .andExpect(jsonPath("$.aval").value(0.424))
                .andExpect(jsonPath("$.pumb").value(0.42));
    }

    @Test
    public void testGetBuyPricesForWrongOperation() throws Exception {
        mockMvc.perform(get("/USD/WRONG_OPERATION"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.message")
                        .value("operation 'WRONG_OPERATION' not supported"));

    }
}
