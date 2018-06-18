package com.aggregator.storage;

import com.aggregator.model.CurrencyRate;
import org.springframework.stereotype.Repository;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Repository
public class CurrencyRatesStorage {

    private Map<String, List<CurrencyRate>> currencyData;

    public CurrencyRatesStorage(Map<String, List<CurrencyRate>> currencyData) {
        this.currencyData = currencyData;
    }

    public Map<String, List<CurrencyRate>> getAllRates() {
        return currencyData;
    }

    public Map<String, CurrencyRate> getRatesForCode(String code) {
        Map<String, CurrencyRate> result = new HashMap<>();

        for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet())
            if (entry.getValue() == null)
                result.put(entry.getKey(), null);
            else
                for (CurrencyRate rate : entry.getValue()) {
                    if (rate.getCode().equals(code)) {
                        result.put(entry.getKey(), rate);
                        break;
                    }
                }
        return result;
    }

    public Map<String, Double> getBuyPriceForCode(String code) {
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet())
            for (CurrencyRate rate : entry.getValue()) {
                if (rate != null && rate.getCode().equals(code) && rate.getBuy() > 0) {
                    result.put(entry.getKey(), rate.getBuy());
                    break;
                }
            }
        return result;
    }

    public Map<String, Double> getSellPricesForCode(String code) {
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet())
            for (CurrencyRate rate : entry.getValue()) {
                if (rate != null && rate.getCode().equals(code) && rate.getSell() > 0) {
                    result.put(entry.getKey(), rate.getSell());
                    break;
                }
            }
        return result;
    }

    public Map<String, Map<String, Map.Entry<String, Double>>> getBestPropositions() {
        //{code, {buy:{bank, value},sell:{bank,value}}}
        Map<String, Map<String, Map.Entry<String, Double>>> result = new HashMap<>();
        Set<String> codes = getAllCodes();

        for (String code : codes) {
            Double maxBuy = Double.MIN_VALUE;
            String maxBuyBank = "";
            Double minSell = Double.MAX_VALUE;
            String minSellBank = "";
            for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet())
                for (CurrencyRate rate : entry.getValue()) {
                    if (rate.getCode().equals(code)) {

                        if (rate.getBuy() >= maxBuy) {
                            maxBuy = rate.getBuy();
                            maxBuyBank = entry.getKey();
                        }

                        if (rate.getSell() <= minSell) {
                            minSell = rate.getSell();
                            minSellBank = entry.getKey();
                        }
                    }
                }

            if (maxBuy.equals(Double.MIN_VALUE)) maxBuy = 0.;
            if (minSell.equals(Double.MAX_VALUE)) minSell = 0.;

            Map.Entry<String, Double> bankBuyEntry = new AbstractMap.SimpleEntry<>(maxBuyBank, maxBuy);
            Map.Entry<String, Double> bankSellEntry = new AbstractMap.SimpleEntry<>(minSellBank, minSell);
            Map<String, Map.Entry<String, Double>> mapEntry = new HashMap<>();
            mapEntry.put("buy", bankBuyEntry);
            mapEntry.put("sell", bankSellEntry);
            result.put(code, mapEntry);

        }
        return result;
    }

    private Set<String> getAllCodes() {
        Set<String> list = new HashSet<>();
        for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet())
            for (CurrencyRate rate : entry.getValue()) {
                list.add(rate.getCode());
            }
        return list;
    }

    public void updateSellPriceForBank(String bank, List<CurrencyRate> entry) {
        currencyData.put(bank, entry);
    }

    public void updateBuyPriceForBank(String bank, List<CurrencyRate> entry) {
        currencyData.put(bank, entry);
    }

    public void deleteRatesForBank(String bank) {
        for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet()) {
            if (entry.getKey().equals(bank))
                entry.getValue().clear();
        }
    }

    public void putData(String s, List<CurrencyRate> rates) {
        currencyData.put(s, rates);
    }
}

