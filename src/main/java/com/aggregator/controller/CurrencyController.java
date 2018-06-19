package com.aggregator.controller;

import com.aggregator.response.JsonResponse;
import com.aggregator.service.CurrencyService;
import com.aggregator.utils.SortMapUtils;
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

    @Autowired
    public CurrencyController(final CurrencyService service) {
        this.currencyService = service;
    }

    @GetMapping(value = {"", "/"}, produces = "application/json")
    public String getRates() {
        return JsonResponse.getJsonFromMap(currencyService.getAllRates());
    }

    @GetMapping(value = "/{code}", produces = "application/json")
    public String getRatesForCode(final @PathVariable(value = "code")
                                              String code) {
        return JsonResponse
                .getJsonFromMap(currencyService.getRatesForCode(code));
    }

    @GetMapping(value = "/{code}/{tag}", produces = "application/json")
    public String getBuyPrices(final @PathVariable(value = "code") String code,
                               final @PathVariable("tag") String tag,
                               final @RequestParam(value = "sort",
                                       required = false) String sort) {
        Map<String, Double> resultMap = null;
        if (tag.equals("buy")) {
            resultMap = currencyService.getBuyPricesForCode(code);
        } else if (tag.equals("sell")) {
            resultMap = currencyService.getSellPricesForCode(code);
        }
        resultMap = sortIfNeeded(sort, resultMap);
        return JsonResponse.getJsonFromMap(resultMap);
    }

    @PutMapping(value = "/{code}/{tag}", produces = "application/json")
    public String updateSellPrice(final @PathVariable("code") String code,
                                  final @PathVariable("tag") String tag,
                                  final @RequestParam("value") String value,
                                  final @RequestParam("bank") String bank) {
        if (tag.equals("buy")) {
            currencyService.updateBuyPriceForBank(bank, code, value);
        } else if (tag.equals("sell")) {
            currencyService.updateSellPriceForBank(bank, code, value);
        }
        return JsonResponse.OK_RESPONSE;

    }

    @DeleteMapping(value = "/", produces = "application/json")
    public String deleteRatesForBank(final @RequestParam("bank") String bank) {
        currencyService.deleteRatesForBank(bank);
        return JsonResponse.OK_RESPONSE;
    }

    @GetMapping(value = "/report", produces = "application/json")
    public String report() {
        return JsonResponse.
                getJsonFromMap(currencyService.getBestPropositions());
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
