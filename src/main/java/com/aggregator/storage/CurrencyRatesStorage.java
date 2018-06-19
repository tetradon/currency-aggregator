package com.aggregator.storage;

import com.aggregator.model.CurrencyRate;
import org.springframework.stereotype.Component;


import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
public final class CurrencyRatesStorage {

    private Map<String, List<CurrencyRate>> currencyData;

    public CurrencyRatesStorage() {
        currencyData = new HashMap<>();
    }

    public CurrencyRatesStorage(final Map<String, List<CurrencyRate>> data) {
        this.currencyData = data;
    }

    public Map<String, List<CurrencyRate>> getAllRates() {
        return currencyData;
    }

    public Map<String, CurrencyRate> getRatesForCode(final String code) {
        Map<String, CurrencyRate> result = new HashMap<>();

        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            if (entry.getValue() == null) {
                result.put(entry.getKey(), null);
            } else {
                for (CurrencyRate rate : entry.getValue()) {
                    if (rate.getCurrencyRateCode().equals(code)) {
                        result.put(entry.getKey(), rate);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public Map<String, Double> getBuyPricesForCode(final String code) {
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            for (CurrencyRate rate : entry.getValue()) {
                if (rate != null
                        && rate.getCurrencyRateCode().equals(code)
                        && rate.getCurrencyRateBuyPrice() > 0) {
                    result.put(entry.getKey(), rate.getCurrencyRateBuyPrice());
                    break;
                }
            }
        }
        return result;
    }

    public Map<String, Double> getSellPricesForCode(final String code) {
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            for (CurrencyRate rate
                    : entry.getValue()) {
                if (rate != null
                        && rate.getCurrencyRateCode().equals(code)
                        && rate.getCurrencyRateSellPrice() > 0) {
                    result.put(entry.getKey(), rate.getCurrencyRateSellPrice());
                    break;
                }
            }
        }
        return result;
    }

    public Map<String,
            Map<String, Map.Entry<String, Double>>> getBestPropositions() {
        //{code, {buy:{bank, value},sell:{bank,value}}}
        Map<String, Map<String, Map.Entry<String, Double>>> result
                = new HashMap<>();
        Set<String> codes = getAllCodes();

        for (String code : codes) {
            Double maxBuy = Double.MIN_VALUE;
            String maxBuyBank = "";
            Double minSell = Double.MAX_VALUE;
            String minSellBank = "";
            for (Map.Entry<String, List<CurrencyRate>> entry
                    : currencyData.entrySet()) {
                for (CurrencyRate rate : entry.getValue()) {
                    if (rate.getCurrencyRateCode().equals(code)) {

                        if (rate.getCurrencyRateBuyPrice() >= maxBuy) {
                            maxBuy = rate.getCurrencyRateBuyPrice();
                            maxBuyBank = entry.getKey();
                        }

                        if (rate.getCurrencyRateSellPrice() <= minSell) {
                            minSell = rate.getCurrencyRateSellPrice();
                            minSellBank = entry.getKey();
                        }
                    }
                }
            }

            if (maxBuy.equals(Double.MIN_VALUE)) {
                maxBuy = 0.;
            }
            if (minSell.equals(Double.MAX_VALUE)) {
                minSell = 0.;
            }

            Map.Entry<String, Double> bankBuyEntry =
                    new AbstractMap.SimpleEntry<>(maxBuyBank, maxBuy);
            Map.Entry<String, Double> bankSellEntry =
                    new AbstractMap.SimpleEntry<>(minSellBank, minSell);
            Map<String, Map.Entry<String, Double>> mapEntry = new HashMap<>();
            mapEntry.put("buy", bankBuyEntry);
            mapEntry.put("sell", bankSellEntry);
            result.put(code, mapEntry);

        }
        return result;
    }

    private Set<String> getAllCodes() {
        Set<String> list = new HashSet<>();
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            for (CurrencyRate rate : entry.getValue()) {
                list.add(rate.getCurrencyRateCode());
            }
        }
        return list;
    }

    public void updateSellPriceForBank(final String bank, final String code,
                                       final Double value) {
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            if (entry.getKey().equals(bank)) {
                for (CurrencyRate rate : entry.getValue()) {
                    if (rate != null
                            && rate.getCurrencyRateCode().equals(code)
                            && rate.getCurrencyRateSellPrice() > 0) {
                        rate.setCurrencyRateSellPrice(value);
                        break;
                    }
                }
            }
        }
    }

    public void updateBuyPriceForBank(final String bank, final String code,
                                      final Double value) {
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            if (entry.getKey().equals(bank)) {
                for (CurrencyRate rate : entry.getValue()) {
                    if (rate != null
                            && rate.getCurrencyRateCode().equals(code)
                            && rate.getCurrencyRateBuyPrice() > 0) {
                        rate.setCurrencyRateBuyPrice(value);
                        break;
                    }
                }
            }
        }
    }

    public void deleteRatesForBank(final String bank) {
        currencyData.remove(bank);
    }

    public void putData(final String s, final List<CurrencyRate> rates) {
        currencyData.put(s, rates);
    }
}

