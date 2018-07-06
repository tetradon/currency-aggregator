package com.aggregator.dao;

import com.aggregator.model.CurrencyRate;
import com.aggregator.utils.BestPropositionsUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
@Profile("file")
public final class CurrencyRatesStorage implements CurrencyRatesDao {

    private Map<String, List<CurrencyRate>> currencyData;

    public CurrencyRatesStorage() {
        currencyData = new HashMap<>();
    }

    public CurrencyRatesStorage(final Map<String, List<CurrencyRate>> data) {
        this.currencyData = data;
    }

    @Override
    public Map<String, List<CurrencyRate>> getAllRates() {
        return currencyData;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public Map<String,
            Map<String,
                    Map.Entry<String, MonetaryAmount>>> getBestPropositions() {
        return BestPropositionsUtils.get(getAllRates());
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

    @Override
    public void updateSellPriceForBank(final String bank, final String code,
                                       final String value) {
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            if (entry.getKey().equals(bank)) {
                for (CurrencyRate rate : entry.getValue()) {
                    if (sellRateIsValid(code, rate)) {
                        rate.setCurrencyRateSellPrice(
                                Double.parseDouble(value));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void updateBuyPriceForBank(final String bank, final String code,
                                      final String value) {
        for (Map.Entry<String, List<CurrencyRate>> entry
                : currencyData.entrySet()) {
            if (entry.getKey().equals(bank)) {
                for (CurrencyRate rate : entry.getValue()) {
                    if (buyRateIsValid(code, rate)) {
                        rate.setCurrencyRateBuyPrice(
                                Double.parseDouble(value));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void deleteRatesForBank(final String bank) {
        currencyData.remove(bank);
    }

    public void putData(final String s, final List<CurrencyRate> rates) {
        currencyData.put(s, rates);
    }
}

