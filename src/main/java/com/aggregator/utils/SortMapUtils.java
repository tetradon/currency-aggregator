package com.aggregator.utils;

import javax.money.MonetaryAmount;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class SortMapUtils {

    private SortMapUtils() {
    }

    public static Map<String, MonetaryAmount> sortDesc(
           final Map<String, MonetaryAmount> unsortedMap) {
        return unsortedMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors
                        .toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static Map<String, MonetaryAmount> sortAsc(
            final Map<String, MonetaryAmount> unsortedMap) {
        return unsortedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors
                        .toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }
}
