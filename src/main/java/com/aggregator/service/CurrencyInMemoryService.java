package com.aggregator.service;


import com.aggregator.model.CurrencyRate;
import com.aggregator.provider.CurrencyProvider;
import com.aggregator.provider.ProviderFactory;
import com.aggregator.storage.CurrencyRatesStorage;
import com.aggregator.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public final class CurrencyInMemoryService
        implements CurrencyService, ServletContextAware {
    private CurrencyRatesStorage currencyRatesStorage;
    private ServletContext servletContext;
    private CurrencyProvider currencyProvider;
    private File folderWithRates;

    public CurrencyInMemoryService(final CurrencyRatesStorage storage,
                                   final File folder) {
        currencyRatesStorage = storage;
        folderWithRates = folder;
    }

    @Autowired
    public CurrencyInMemoryService(final CurrencyRatesStorage storage) {
        currencyRatesStorage = storage;
    }

    @PostConstruct
    public void postConstruct() {
        folderWithRates = new File(
                servletContext.getRealPath("/WEB-INF/rates/"));
        List<CurrencyRate> rates;
        for (final File fileEntry
                : Objects.requireNonNull(
                        folderWithRates.listFiles())) {
            currencyProvider = ProviderFactory
                    .getProvider(FileUtils.getExtension(fileEntry));
            rates = Objects.requireNonNull(currencyProvider).getData(fileEntry);
            currencyRatesStorage
                    .putData(FileUtils
                            .stripExtension(fileEntry.getName()), rates);
        }
    }

    @Override
    public Map<String, CurrencyRate> getRatesForCode(final String code) {
        return currencyRatesStorage.getRatesForCode(code);
    }

    @Override
    public Map<String, List<CurrencyRate>> getAllRates() {
        return currencyRatesStorage.getAllRates();
    }

    @Override
    public Map<String, Double> getBuyPricesForCode(final String code) {
        return currencyRatesStorage.getBuyPricesForCode(code);
    }

    @Override
    public Map<String, Double> getSellPricesForCode(final String code) {
        return currencyRatesStorage.getSellPricesForCode(code);
    }

    @Override
    public void updateSellPriceForBank(final String bank, final String code,
                                       final String value) {
        File file = FileUtils.findFileByName(folderWithRates, bank);
        currencyProvider = ProviderFactory
                .getProvider(FileUtils.getExtension(file));
        currencyProvider.updateSellPrice(file, code, Double.valueOf(value));
        currencyRatesStorage
                .updateSellPriceForBank(bank, code, Double.valueOf(value));
    }

    @Override
    public void updateBuyPriceForBank(final String bank, final String code,
                                      final String value) {
        File file = FileUtils.findFileByName(folderWithRates, bank);
        currencyProvider = ProviderFactory
                .getProvider(FileUtils.getExtension(file));
        currencyProvider.updateBuyPrice(file, code, Double.valueOf(value));
        currencyRatesStorage
                .updateBuyPriceForBank(bank, code, Double.valueOf(value));
    }

    @Override
    public void deleteRatesForBank(final String bank) {
        File file = FileUtils.findFileByName(folderWithRates, bank);
        currencyProvider = ProviderFactory
                .getProvider(FileUtils.getExtension(file));
        currencyProvider.deleteRatesForBank(file);
        currencyRatesStorage.deleteRatesForBank(bank);
    }

    @Override
    public Map<String,
            Map<String, Map.Entry<String, Double>>> getBestPropositions() {
        return currencyRatesStorage.getBestPropositions();
    }

    @Override
    public void setServletContext(final ServletContext context) {
        servletContext = context;
    }
}
