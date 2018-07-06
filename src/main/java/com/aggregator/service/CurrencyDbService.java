package com.aggregator.service;

import com.aggregator.dao.CurrencyRatesDao;
import com.aggregator.model.CurrencyRate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;

import java.util.List;
import java.util.Map;

@Service
@Profile("db")
public final class CurrencyDbService
        implements CurrencyService {

    private CurrencyRatesDao currencyRatesDao;

    @Autowired
    public CurrencyDbService(CurrencyRatesDao dao) {
        currencyRatesDao = dao;
    }

    @Override
    public Map<String, CurrencyRate> getRatesForCode(String code) {
        return currencyRatesDao.getRatesForCode(code);
    }

    @Override
    public Map<String, List<CurrencyRate>> getAllRates() {
        return currencyRatesDao.getAllRates();
    }

    @Override
    public Map<String, MonetaryAmount> getBuyPricesForCode(String code) {
        return currencyRatesDao.getBuyPricesForCode(code);
    }

    @Override
    public Map<String, MonetaryAmount> getSellPricesForCode(String code) {
        return currencyRatesDao.getSellPricesForCode(code);
    }

    @Override
    public void updateSellPriceForBank(String bank, String code, String value) {
        currencyRatesDao.updateSellPriceForBank(bank, code, value);
    }

    @Override
    public void updateBuyPriceForBank(String bank, String code, String value) {
        currencyRatesDao.updateBuyPriceForBank(bank, code, value);
    }

    @Override
    public void deleteRatesForBank(String bank) {
        currencyRatesDao.deleteRatesForBank(bank);
    }

    @Override
    public Map<String,
            Map<String,
                    Map.Entry<String, MonetaryAmount>>> getBestPropositions() {
        return currencyRatesDao.getBestPropositions();
    }
}
