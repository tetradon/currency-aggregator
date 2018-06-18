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

import java.util.Map;

@RestController
public class CurrencyController {
    private final CurrencyService service;

    @Autowired
    public CurrencyController(CurrencyService service) {
        this.service = service;
    }

    @GetMapping(value = {"", "/"}, produces = "application/json")
    public String getRates() {
        return JsonResponse.getJsonFromMap(service.getAllRates());
    }

    @GetMapping(value = "/{code}", produces = "application/json")
    public String getRatesForCode(@PathVariable(value = "code") String code) {
        return JsonResponse.getJsonFromMap(service.getRatesForCode(code));
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
        return JsonResponse.getJsonFromMap(resultMap);
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
        return JsonResponse.okResponse;

    }

    @DeleteMapping(value = "/", produces = "application/json")
    public String deleteRatesForBank(@RequestParam("bank") String bank) {
        service.deleteRatesForBank(bank);
        return JsonResponse.okResponse;
    }

    @GetMapping(value = "/report", produces = "application/json")
    public String report() {
        return JsonResponse.getJsonFromMap(service.getBestPropositions());
    }

    private Map<String, Double> sortIfNeeded(@RequestParam(value = "sort", required = false) String sort, Map<String, Double> resultMap) {
        if (sort != null && sort.equals("asc")) {
            resultMap = SortMapUtils.sortAsc(resultMap);
        } else if (sort != null && sort.equals("desc")) {
            resultMap = SortMapUtils.sortDesc(resultMap);
        }
        return resultMap;
    }
}
