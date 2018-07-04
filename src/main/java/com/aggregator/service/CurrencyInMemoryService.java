package com.aggregator.service;

import com.aggregator.exception.BankNotFoundException;
import com.aggregator.exception.CurrencyNotFoundException;
import com.aggregator.model.CurrencyRate;
import com.aggregator.provider.CurrencyProvider;
import com.aggregator.provider.ProviderFactory;
import com.aggregator.storage.CurrencyRatesStorage;
import com.aggregator.utils.FileUtils;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public final class CurrencyInMemoryService
        implements CurrencyService {
    private CurrencyRatesStorage currencyRatesStorage;
    private CurrencyProvider currencyProvider;
    private File folderWithRates;

    public CurrencyInMemoryService(final File folder) {
        folderWithRates = folder;
        currencyRatesStorage = new CurrencyRatesStorage();
        List<CurrencyRate> rates;

        File[] filesInFolder = folderWithRates.listFiles();
        if (filesInFolder != null) {
            for (final File fileEntry
                    : filesInFolder) {
                currencyProvider = ProviderFactory
                        .getProvider(FileUtils.getExtension(fileEntry));
                rates = Objects.requireNonNull(currencyProvider).
                        getData(fileEntry);
                currencyRatesStorage
                        .putData(FileUtils
                                .stripExtension(fileEntry.getName()), rates);
            }
        }
    }

    public CurrencyInMemoryService(final CurrencyRatesStorage storage) {
        currencyRatesStorage = storage;
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
    public Map<String, MonetaryAmount> getBuyPricesForCode(final String code) {
        return currencyRatesStorage.getBuyPricesForCode(code);
    }

    @Override
    public Map<String, MonetaryAmount> getSellPricesForCode(final String code) {
        return currencyRatesStorage.getSellPricesForCode(code);
    }

    @Override
    public void updateSellPriceForBank(final String bank, final String code,
                                       final String value) {
        File file = getFirstFile(bank);
        if (bankContainsCode(bank, code)) {
            currencyProvider = ProviderFactory
                    .getProvider(FileUtils.getExtension(file));
            currencyProvider.updateSellPrice(file, code, Double.valueOf(value));
            currencyRatesStorage
                    .updateSellPriceForBank(bank, code, Double.valueOf(value));
        } else {
            throw new CurrencyNotFoundException(code);
        }
    }

    @Override
    public void updateBuyPriceForBank(final String bank, final String code,
                                      final String value) {
        File file = getFirstFile(bank);
        if (bankContainsCode(bank, code)) {
            currencyProvider = ProviderFactory
                    .getProvider(FileUtils.getExtension(file));
            currencyProvider.updateBuyPrice(file, code, Double.valueOf(value));
            currencyRatesStorage
                    .updateBuyPriceForBank(bank, code, Double.valueOf(value));
        } else {
            throw new CurrencyNotFoundException(code);
        }
    }

    private boolean bankContainsCode(String bank, String code) {
        return currencyRatesStorage.getAllCodesForBank(bank).contains(code);
    }

    @Override
    public void deleteRatesForBank(final String bank) {
        File file = getFirstFile(bank);
        currencyProvider = ProviderFactory
                .getProvider(FileUtils.getExtension(file));
        currencyProvider.deleteRatesForBank(file);
        currencyRatesStorage.deleteRatesForBank(bank);
    }

    @Override
    public Map<String,
            Map<String,
                    Map.Entry<String, MonetaryAmount>>> getBestPropositions() {
        return currencyRatesStorage.getBestPropositions();
    }

    private File getFirstFile(String bank) {
        File[] foundFiles
                = FileUtils.findFilesInFolderByName(folderWithRates, bank);
        if (foundFiles.length == 0) {
            throw new BankNotFoundException(bank);
        }
        return foundFiles[0];
    }
}
