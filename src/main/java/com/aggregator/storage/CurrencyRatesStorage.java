package com.aggregator.storage;

import com.aggregator.model.CurrencyRate;
import com.aggregator.utils.ListUtils;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Component;


import javax.money.MonetaryAmount;
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

    public Map<String, MonetaryAmount> getBuyPricesForCode(final String code) {
        Map<String, MonetaryAmount> result = new HashMap<>();
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            for (CurrencyRate rate : entry.getValue()) {
                if (buyRateIsValid(code, rate)) {
                    result.put(entry.getKey(), rate.getCurrencyRateBuyPrice());
                    break;
                }
            }
        }
        return result;
    }

    private boolean buyRateIsValid(String code, CurrencyRate rate) {
        return rate != null
                && rate.getCurrencyRateCode().equals(code)
                && rate.getCurrencyRateBuyPrice().isPositive();
    }

    public Map<String, MonetaryAmount> getSellPricesForCode(final String code) {
        Map<String, MonetaryAmount> result = new HashMap<>();
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            for (CurrencyRate rate
                    : entry.getValue()) {
                if (sellRateIsValid(code, rate)) {
                    result.put(entry.getKey(), rate.getCurrencyRateSellPrice());
                    break;
                }
            }
        }
        return result;
    }

    private boolean sellRateIsValid(String code, CurrencyRate rate) {
        return rate != null
                && rate.getCurrencyRateCode().equals(code)
                && rate.getCurrencyRateSellPrice().isPositive();
    }

    public Map<String,
            Map<String,
                    Map.Entry<String, MonetaryAmount>>> getBestPropositions() {

        Map<String, Map<String, Map.Entry<String, MonetaryAmount>>> result
                = new HashMap<>();
        Set<String> codes = getAllCodes();

        for (String code : codes) {
            MonetaryAmount maxBuy = Money.of(Double.MIN_VALUE, code);
            String maxBuyBank = "";
            MonetaryAmount minSell = Money.of(Double.MAX_VALUE, code);
            String minSellBank = "";
            for (Map.Entry<String, List<CurrencyRate>> entry
                    : currencyData.entrySet()) {

                List<CurrencyRate> rates = ListUtils
                        .filterOutByCode(code, entry);

                if (rates.isEmpty()) {
                    continue;
                }

                List<CurrencyRate> sortedBuyRates =
                        ListUtils.sortByBuyPrice(rates);
                List<CurrencyRate> sortedSellRates =
                        ListUtils.sortBySellPrice(rates);
                MonetaryAmount localMaxBuy =
                        ListUtils.getLast(sortedBuyRates);
                MonetaryAmount localMinSell =
                        ListUtils.getFirst(sortedSellRates);

                if (localMaxBuy.isGreaterThan(maxBuy)) {
                    maxBuy = localMaxBuy;
                    maxBuyBank = entry.getKey();
                }

                if (localMinSell.isLessThan(minSell)) {
                    minSell = localMinSell;
                    minSellBank = entry.getKey();
                }
            }

            Map.Entry<String, MonetaryAmount> bankBuyEntry =
                    new AbstractMap.SimpleEntry<>(maxBuyBank, maxBuy);
            Map.Entry<String, MonetaryAmount> bankSellEntry =
                    new AbstractMap.SimpleEntry<>(minSellBank, minSell);
            Map<String, Map.Entry<String, MonetaryAmount>> mapEntry
                    = new HashMap<>();
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

    public Set<String> getAllCodesForBank(String bank) {
        Set<String> codes = new HashSet<>();
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            if (entry.getKey().equals(bank)) {
                for (CurrencyRate rate : entry.getValue()) {
                    codes.add(rate.getCurrencyRateCode());
                }
                break;
            }
        }
        return codes;
    }

    public void updateSellPriceForBank(final String bank, final String code,
                                       final Double value) {
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            if (entry.getKey().equals(bank)) {
                for (CurrencyRate rate : entry.getValue()) {
                    if (sellRateIsValid(code, rate)) {
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
                    if (buyRateIsValid(code, rate)) {
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

