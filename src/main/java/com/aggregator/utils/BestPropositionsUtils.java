package com.aggregator.utils;

import com.aggregator.model.CurrencyRate;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class BestPropositionsUtils {
    private BestPropositionsUtils() {
    }

    public static Map<String,
            Map<String, Map.Entry<String, MonetaryAmount>>>
    get(Map<String, List<CurrencyRate>> currencyData) {

        Map<String, Map<String, Map.Entry<String, MonetaryAmount>>> result
                = new HashMap<>();
        Set<String> codes = getAllCodes(currencyData);
        for (String code : codes) {
            MonetaryAmount maxBuy = Money.of(Double.MIN_VALUE, code);
            String maxBuyBank = "";
            MonetaryAmount minSell = Money.of(Double.MAX_VALUE, code);
            String minSellBank = "";
            for (Map.Entry<String, List<CurrencyRate>> entry
                    : currencyData.entrySet()) {

                List<CurrencyRate> rates = BestPropositionsUtils
                        .filterOutByCode(code, entry);

                if (rates.isEmpty()) {
                    continue;
                }

                List<CurrencyRate> sortedBuyRates =
                        BestPropositionsUtils.sortByBuyPrice(rates);
                List<CurrencyRate> sortedSellRates =
                        BestPropositionsUtils.sortBySellPrice(rates);
                MonetaryAmount localMaxBuy =
                        BestPropositionsUtils.getLast(sortedBuyRates);
                MonetaryAmount localMinSell =
                        BestPropositionsUtils.getFirst(sortedSellRates);

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

    private static Set<String> getAllCodes(Map<String,
            List<CurrencyRate>> currencyData) {
        Set<String> list = new HashSet<>();
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            for (CurrencyRate rate : entry.getValue()) {
                list.add(rate.getCurrencyRateCode());
            }
        }
        return list;
    }

    private static List<CurrencyRate> filterOutByCode(
            String code, Map.Entry<String, List<CurrencyRate>> entry) {
        return entry.getValue()
                .stream()
                .filter(rate -> rate.getCurrencyRateCode().equals(code))
                .collect(Collectors.toList());
    }

    private static MonetaryAmount getFirst(List<CurrencyRate> sortedSellRates) {
        return sortedSellRates
                .get(0).getCurrencyRateSellPrice();
    }

    private static MonetaryAmount getLast(List<CurrencyRate> sortedBuyRates) {
        return sortedBuyRates
                .get(sortedBuyRates.size() - 1)
                .getCurrencyRateBuyPrice();
    }

    private static List<CurrencyRate> sortBySellPrice(
            List<CurrencyRate> rates) {
        return rates
                .stream()
                .sorted(new SellComparator())
                .collect(Collectors.toList());
    }

    private static List<CurrencyRate> sortByBuyPrice(List<CurrencyRate> rates) {
        return rates
                .stream()
                .sorted(new BuyComparator())
                .collect(Collectors.toList());
    }

    private static class SellComparator
            implements Comparator<CurrencyRate>, Serializable {
        public int compare(CurrencyRate r1, CurrencyRate r2) {
            return r1.getCurrencyRateSellPrice()
                    .compareTo(r2.getCurrencyRateSellPrice());
        }
    }

    private static class BuyComparator
            implements Comparator<CurrencyRate>, Serializable {
        public int compare(CurrencyRate r1, CurrencyRate r2) {
            return r1.getCurrencyRateBuyPrice()
                    .compareTo(r2.getCurrencyRateBuyPrice());
        }
    }
}
