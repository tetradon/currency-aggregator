package com.aggregator.dao;

import com.aggregator.model.CurrencyRate;

import javax.money.MonetaryAmount;
import java.util.List;
import java.util.Map;

public interface CurrencyRatesDao {
    Map<String, CurrencyRate> getRatesForCode(String code);

    Map<String, List<CurrencyRate>> getAllRates();

    Map<String, MonetaryAmount> getBuyPricesForCode(String code);

    Map<String, MonetaryAmount> getSellPricesForCode(String code);

    void updateSellPriceForBank(String bank, String code, String value);

    void updateBuyPriceForBank(String bank, String code, String value);

    void deleteRatesForBank(String bank);

    Map<String, Map<String,
            Map.Entry<String, MonetaryAmount>>> getBestPropositions();
}
