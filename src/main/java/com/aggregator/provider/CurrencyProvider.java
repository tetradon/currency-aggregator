package com.aggregator.provider;

import com.aggregator.model.CurrencyRate;

import java.io.File;
import java.util.List;

public interface CurrencyProvider {
    List<CurrencyRate> getData(File file);

    void updateBuyPrice(File file, String code, Double newValue);

    void updateSellPrice(File file, String code, Double newValue);

    void deleteRatesForBank(File file);
}
