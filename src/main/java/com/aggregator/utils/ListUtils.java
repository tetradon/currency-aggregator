package com.aggregator.utils;

import com.aggregator.model.CurrencyRate;

import javax.money.MonetaryAmount;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ListUtils {
    private ListUtils() {
    }

    public static List<CurrencyRate> filterOutByCode(
            String code, Map.Entry<String, List<CurrencyRate>> entry) {
        return entry.getValue()
                .stream()
                .filter(rate -> rate.getCurrencyRateCode().equals(code))
                .collect(Collectors.toList());
    }

    public static MonetaryAmount getFirst(List<CurrencyRate> sortedSellRates) {
        return sortedSellRates
                .get(0).getCurrencyRateSellPrice();
    }

    public static MonetaryAmount getLast(List<CurrencyRate> sortedBuyRates) {
        return sortedBuyRates
                .get(sortedBuyRates.size() - 1)
                .getCurrencyRateBuyPrice();
    }

    public static List<CurrencyRate> sortBySellPrice(List<CurrencyRate> rates) {
        return rates
                .stream()
                .sorted(new SellComparator())
                .collect(Collectors.toList());
    }

    public static List<CurrencyRate> sortByBuyPrice(List<CurrencyRate> rates) {
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
