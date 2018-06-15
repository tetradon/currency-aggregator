package com.aggregator.controller;


import com.aggregator.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.aggregator.response.JsonResponse.getJsonFromMap;
import static com.aggregator.response.JsonResponse.okResponse;
import static com.aggregator.utils.SortMapUtils.sortAsc;
import static com.aggregator.utils.SortMapUtils.sortDesc;

@RestController
public class CurrencyController {
    private final CurrencyService service;


    @Autowired
    public CurrencyController(CurrencyService service) {
        this.service = service;
    }

    @GetMapping(value = {"", "/"}, produces = "application/json")
    public String getRates() {
        return getJsonFromMap(service.getAllRates());
    }

    @GetMapping(value = "/{code}", produces = "application/json")
    public String getRatesForCode(@PathVariable(value = "code") String code) {
        return getJsonFromMap(service.getRatesForCode(code));
    }

    @GetMapping(value = "/{code}/{tag}", produces = "application/json")
    public String getBuyPrices(@PathVariable(value = "code") String code,
                               @PathVariable("tag") String tag,
                               @RequestParam(value = "sort", required = false) String sort) {
        Map<String, Double> resultMap = null;
        if (tag.equals("buy"))
            resultMap = service.getBuyPricesForCode(code);
        else if (tag.equals("sell"))
            resultMap = service.getSellPricesForCode(code);
        resultMap = sortIfNeeded(sort, resultMap);
        return getJsonFromMap(resultMap);
    }

    @PutMapping(value = "/{code}/{tag}", produces = "application/json")
    public String updateSellPrice(@PathVariable("code") String code,
                                  @PathVariable("tag") String tag,
                                  @RequestParam("value") String value,
                                  @RequestParam("bank") String bank) {
        if (tag.equals("buy"))
            service.updateBuyPriceForBank(bank, code, value);
        else if (tag.equals("sell"))
            service.updateSellPriceForBank(bank, code, value);
        return okResponse;

    }

    @DeleteMapping(value = "/", produces = "application/json")
    public String deleteRatesForBank(@RequestParam("bank") String bank) {
        service.deleteRatesForBank(bank);
        return okResponse;
    }

    @GetMapping(value = "/report", produces = "application/json")
    public String report() {
        return getJsonFromMap(service.getBestPropositions());
    }

    private Map<String, Double> sortIfNeeded(@RequestParam(value = "sort", required = false) String sort, Map<String, Double> resultMap) {
        if (sort != null && sort.equals("asc")) {
            resultMap = sortAsc(resultMap);
        } else if (sort != null && sort.equals("desc")) {
            resultMap = sortDesc(resultMap);
        }
        return resultMap;
    }
}
