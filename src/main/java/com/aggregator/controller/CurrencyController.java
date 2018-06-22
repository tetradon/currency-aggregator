package com.aggregator.controller;

import com.aggregator.response.JsonResponse;
import com.aggregator.service.CurrencyService;
import com.aggregator.utils.SortMapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.HashMap;
import java.util.Map;

@RestController
public final class CurrencyController {
    private final CurrencyService currencyService;

    private static final Logger log =
            LogManager.getLogger(CurrencyController.class);
    private static final String GET_REQUEST = "GET request to /";
    private static final String RESPONSE = "Response: ";

    @Autowired
    public CurrencyController(final CurrencyService service) {
        this.currencyService = service;
    }

    @GetMapping(value = {"", "/"}, produces = "application/json")
    public String getRates() {
        log.info(GET_REQUEST);
        Map response = currencyService.getAllRates();
        log.info(RESPONSE + response);
        return JsonResponse.getJsonFromMap(response);
    }

    @GetMapping(value = "/{code}", produces = "application/json")
    public String getRatesForCode(final @PathVariable(value = "code")
                                              String code) {
        log.info(GET_REQUEST + code);
        Map response = currencyService.getRatesForCode(code);
        log.info(RESPONSE + response);
        return JsonResponse
                .getJsonFromMap(response);
    }

    @GetMapping(value = "/{code}/{tag}", produces = "application/json")
    public String getBuyPrices(final @PathVariable(value = "code") String code,
                               final @PathVariable("tag") String tag,
                               final @RequestParam(value = "sort",
                                       required = false) String sort) {
        Map<String, Double> resultMap = null;
        log.info(GET_REQUEST + code + "/" + tag
                + " with param sort = " + sort);
        if (tag.equals("buy")) {
            resultMap = currencyService.getBuyPricesForCode(code);
        } else if (tag.equals("sell")) {
            resultMap = currencyService.getSellPricesForCode(code);
        }
        resultMap = sortIfNeeded(sort, resultMap);
        log.info(RESPONSE + resultMap);
        return JsonResponse.getJsonFromMap(resultMap);
    }

    @PutMapping(value = "/{code}/{tag}", produces = "application/json")
    public String updateSellPrice(final @PathVariable("code") String code,
                                  final @PathVariable("tag") String tag,
                                  final @RequestParam("value") String value,
                                  final @RequestParam("bank") String bank) {
        log.info("PUT request to /" + code + "/" + tag
                + " with params value = " + value + ",bank = " + bank);
        if (tag.equals("buy")) {
            currencyService.updateBuyPriceForBank(bank, code, value);
        } else if (tag.equals("sell")) {
            currencyService.updateSellPriceForBank(bank, code, value);
        }
        log.info(RESPONSE + JsonResponse.OK_RESPONSE);
        return JsonResponse.OK_RESPONSE;

    }

    @DeleteMapping(value = "/", produces = "application/json")
    public String deleteRatesForBank(final @RequestParam("bank") String bank) {
        log.info("DELETE request to / with param bank = " + bank);
        currencyService.deleteRatesForBank(bank);
        log.info(RESPONSE + JsonResponse.OK_RESPONSE);
        return JsonResponse.OK_RESPONSE;
    }

    @GetMapping(value = "/report", produces = "application/json")
    public String report() {
        log.info("GET request to /report");
        Map response = currencyService.getBestPropositions();
        log.info(RESPONSE + response);
        return JsonResponse.
                getJsonFromMap(response);
    }

    private Map<String, Double> sortIfNeeded(
            @RequestParam(value = "sort", required = false)
                    final String sort,
                    final Map<String, Double> unsortedMap) {
        if (sort != null) {
            Map<String, Double> sortedMap = new HashMap<>();
            if (sort.equals("asc")) {
                sortedMap = SortMapUtils.sortAsc(unsortedMap);
            } else if (sort.equals("desc")) {
                sortedMap = SortMapUtils.sortDesc(unsortedMap);
            }
            return sortedMap;
        }
        return unsortedMap;
    }
}
