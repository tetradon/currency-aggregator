package com.aggregator.service;


import com.aggregator.model.CurrencyRate;

import java.util.List;
import java.util.Map;

public interface CurrencyService {
    Map<String, CurrencyRate> getRatesForCode(String code);

    Map<String, List<CurrencyRate>> getAllRates();

    Map<String, Double> getBuyPricesForCode(String code);

    Map<String, Double> getSellPricesForCode(String code);

    void updateSellPriceForBank(String bank, String code, String value);

    void updateBuyPriceForBank(String bank, String code, String value);

    void deleteRatesForBank(String bank);

    Map<String, Map<String, Map.Entry<String, Double>>> getBestPropositions();
}
