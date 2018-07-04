package integration;

import com.aggregator.controller.ControllerExceptionHandler;
import com.aggregator.controller.CurrencyController;
import com.aggregator.service.CurrencyInMemoryService;
import com.aggregator.service.CurrencyService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration

public class CurrencyAggregatorIntegrationTest {

    private MockMvc mockMvc;

    private File folder;

    @Before
    public void setUp() throws IOException {
        setUpFolder();

        CurrencyService currencyService = new CurrencyInMemoryService(folder);
        CurrencyController controller = new CurrencyController(currencyService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    private void setUpFolder() throws IOException {
        folder = Files.createTempDirectory("temp_rates").toFile();
        createXmlInFolder();
        createCsvInFolder();
        createJsonInFolder();
    }

    private void createJsonInFolder() throws IOException {
        File file = new File(folder, "otp.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("[\n" +
                    "  {\n" +
                    "    \"code\": \"USD\",\n" +
                    "    \"buy\": 25.9,\n" +
                    "    \"sell\": 26.1\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"code\": \"CHF\",\n" +
                    "    \"buy\": 25.75,\n" +
                    "    \"sell\": 26.35\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"code\": \"EUR\",\n" +
                    "    \"buy\": 30.2,\n" +
                    "    \"sell\": 30.75\n" +
                    "  }\n" +
                    "]\n");
            writer.flush();
        }
    }

    private void createCsvInFolder() throws IOException {
        File file = new File(folder, "aval.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("code,buy,sell\n" +
                    "EUR,30.3,30.8\n" +
                    "USD,25.8,26.2\n" +
                    "RUB,0.414,0.424\n" +
                    "CHF,25.8,26.45\n" +
                    "GBP,34.4,35.0"
            );
            writer.flush();
        }
    }

    private void createXmlInFolder() {
        File file = new File(folder, "pumb.xml");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(
                    "<rates>\n" +
                            "    <rate>\n" +
                            "        <code>USD</code>\n" +
                            "        <buy>26.0</buy>\n" +
                            "        <sell>26.2</sell>\n" +
                            "    </rate>\n" +
                            "    <rate>\n" +
                            "        <code>RUB</code>\n" +
                            "        <buy>0.41</buy>\n" +
                            "        <sell>0.42</sell>\n" +
                            "    </rate>\n" +
                            "    <rate>\n" +
                            "        <code>EUR</code>\n" +
                            "        <buy>30.5</buy>\n" +
                            "        <sell>30.9</sell>\n" +
                            "    </rate>\n" +
                            "    <rate>\n" +
                            "        <code>CHF</code>\n" +
                            "        <buy>25.5</buy>\n" +
                            "        <sell>26.3</sell>\n" +
                            "    </rate>\n" +
                            "    <rate>\n" +
                            "        <code>GBP</code>\n" +
                            "        <buy>34.3</buy>\n" +
                            "        <sell>35.0</sell>\n" +
                            "    </rate>\n" +
                            "</rates>");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
                .andExpect(jsonPath("$.aval.buy").value(25.8))
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
    public void testGetSellPricesForUsdSortAsc() throws Exception {
    mockMvc.perform(get("/USD/buy?sort=asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=ISO-8859-1"))
                .andExpect(jsonPath("$.*", contains(25.8,25.9,26.0)));
    }

    @Test
    public void testGetSellPricesForUsdSortDesc() throws Exception {
        mockMvc.perform(get("/USD/buy?sort=desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=ISO-8859-1"))
                .andExpect(jsonPath("$.*", contains(26.0,25.9,25.8)));
    }

    @Test
    public void testGetBuyPricesForWrongOperation() throws Exception {
        mockMvc.perform(get("/USD/WRONG_OPERATION"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.message")
                        .value("operation 'WRONG_OPERATION' not supported"));
    }

    @Test
    public void testGetBestPropositions() throws Exception {
        mockMvc.perform(get("/report"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE + ";charset=ISO-8859-1"))
                .andExpect(jsonPath("$.CHF.buy.aval").value(25.8))
                .andExpect(jsonPath("$.CHF.sell.pumb").value(26.3))
                .andExpect(jsonPath("$.EUR.buy.pumb").value(30.5))
                .andExpect(jsonPath("$.EUR.sell.otp").value(30.75))
                .andExpect(jsonPath("$.GBP.buy.aval").value(34.4))
                .andExpect(jsonPath("$.GBP.sell.aval").value(35.0))
                .andExpect(jsonPath("$.USD.buy.pumb").value(26.0))
                .andExpect(jsonPath("$.USD.sell.otp").value(26.1))
                .andExpect(jsonPath("$.RUB.buy.aval").value(0.414))
                .andExpect(jsonPath("$.RUB.sell.pumb").value(0.42));
    }


    @Test
    public void testUpdateUsdBuyPriceForOtp() throws Exception {
        mockMvc.perform(put("/USD/buy")
                .param("bank", "otp")
                .param("value", "0.01"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/USD/buy"))
                .andExpect(jsonPath("$.otp").value(0.01));
    }

    @Test
    public void testUpdateRubSellPriceForPumb() throws Exception {
        mockMvc.perform(put("/USD/sell")
                .param("bank", "pumb")
                .param("value", "0.01"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/USD/sell"))
                .andExpect(jsonPath("$.pumb").value(0.01));
    }

    @Test
    public void testUpdatePriceWithWrongOperation() throws Exception {
        mockMvc.perform(put("/USD/WRONG_OPERATION")
                .param("bank", "pumb")
                .param("value", "0.01"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.message")
                        .value("operation 'WRONG_OPERATION' not supported"));


        mockMvc.perform(get("/USD/sell"))
                .andExpect(jsonPath("$.pumb").value(26.2));
    }

    @Test
    public void testUpdatePriceForWrongBank() throws Exception {
        mockMvc.perform(put("/USD/buy")
                .param("bank", "WRONG_BANK")
                .param("value", "0.01"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.message")
                        .value("'WRONG_BANK' does not exist"));
    }

    @Test
    public void testUpdatePriceForWrongCurrency() throws Exception {
        mockMvc.perform(put("/WRONG_CURRENCY/buy")
                .param("bank", "pumb")
                .param("value", "0.01"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.message")
                        .value("currency 'WRONG_CURRENCY' not found"));
    }

    @Test
    public void testDeletePumb() throws Exception {
        mockMvc.perform(delete("/")
                .param("bank", "pumb"))
                .andExpect(status().isOk());
        MvcResult result = mockMvc.perform(get("/USD")).andReturn();
        assertFalse(result.getResponse().getContentAsString().contains("pumb"));
    }

    @Test
    public void testDeleteWrongBank() throws Exception {
        mockMvc.perform(delete("/")
                .param("bank", "WRONG_BANK"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.message")
                        .value("'WRONG_BANK' does not exist"));
    }

    @Test
    public void testUpdateWithoutBankParam() throws Exception {
        mockMvc.perform(put("/USD/buy")
                .param("value", "0.01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateWithoutValueParam() throws Exception {
        mockMvc.perform(put("/USD/buy")
                .param("bank", "aval"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateWithoutParams() throws Exception {
        mockMvc.perform(put("/USD/buy"))
                .andExpect(status().isBadRequest());
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
