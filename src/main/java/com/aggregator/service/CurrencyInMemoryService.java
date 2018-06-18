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
public class CurrencyInMemoryService implements CurrencyService,ServletContextAware {
    private final CurrencyRatesStorage storage;
    private ServletContext servletContext;
    private CurrencyProvider currencyProvider;

    @PostConstruct
    public void postConstruct() {
        File folder = new File(servletContext.getRealPath("/WEB-INF/rates/"));
        List<CurrencyRate> rates;
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            currencyProvider = ProviderFactory.getProvider(FileUtils.getExtension(fileEntry));
            rates = Objects.requireNonNull(currencyProvider).getData(fileEntry);
            storage.putData(FileUtils.stripExtension(fileEntry.getName()), rates);
        }
    }

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
        File file = FileUtils.findFileByName(servletContext, bank);
        currencyProvider = ProviderFactory.getProvider(FileUtils.getExtension(file));
        currencyProvider.updateSellPrice(file, code, Double.valueOf(value));
        List<CurrencyRate> entry = currencyProvider.getData(file);
        storage.updateSellPriceForBank(bank, entry);
    }

    @Override
    public void updateBuyPriceForBank(String bank, String code, String value) {
        File file = FileUtils.findFileByName(servletContext, bank);
        currencyProvider = ProviderFactory.getProvider(FileUtils.getExtension(file));
        currencyProvider.updateBuyPrice(file, code, Double.valueOf(value));
        List<CurrencyRate> entry = currencyProvider.getData(file);
        storage.updateBuyPriceForBank(bank, entry);
    }

    @Override
    public void deleteRatesForBank(String bank) {
        File file = FileUtils.findFileByName(servletContext, bank);
        currencyProvider = ProviderFactory.getProvider(FileUtils.getExtension(file));
        currencyProvider.deleteRatesForBank(file);
        storage.deleteRatesForBank(bank);
    }

    @Override
    public Map<String, Map<String, Map.Entry<String, Double>>> getBestPropositions() {
        return storage.getBestPropositions();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
