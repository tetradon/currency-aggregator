package com.aggregator.service;


import com.aggregator.model.CurrencyRate;
import com.aggregator.storage.CurrencyRatesStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CurrencyInMemoryService implements CurrencyService {
    private final CurrencyRatesStorage storage;

    @Autowired
    public CurrencyInMemoryService(CurrencyRatesStorage storage) {
        this.storage = storage;
    }

    @Override
    public Map<String, CurrencyRate> getRatesForCode(String code) {
        return storage.getRatesForCode(code);
    }

    @Override
    public Map<String, List<CurrencyRate>> getAllRates() {
        return storage.getAllRates();
    }

    @Override
    public Map<String, Double> getBuyPricesForCode(String code) {
        return storage.getBuyPriceForCode(code);
    }

    @Override
    public Map<String, Double> getSellPricesForCode(String code) {
        return storage.getSellPricesForCode(code);
    }

    @Override
    public void updateSellPriceForBank(String bank, String code, String value) {
        storage.updateSellPriceForBank(bank, code, value);
    }

    @Override
    public void updateBuyPriceForBank(String bank, String code, String value) {
        storage.updateBuyPriceForBank(bank, code, value);
    }

    @Override
    public void deleteRatesForBank(String bank) {
        storage.deleteRatesForBank(bank);
    }

    @Override
    public Map<String, Map<String, Map.Entry<String, Double>>> getBestPropositions() {
        return storage.getBestPropositions();
    }
}
