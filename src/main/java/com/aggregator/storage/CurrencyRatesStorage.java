package com.aggregator.storage;

import com.aggregator.model.CurrencyRate;
import com.aggregator.provider.CurrencyProvider;
import com.aggregator.provider.ProviderFactory;
import com.aggregator.utils.FileUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.File;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


@Repository
public class CurrencyRatesStorage implements ServletContextAware {
    private CurrencyProvider currencyProvider;
    private Map<String, List<CurrencyRate>> currencyData;
    private ServletContext servletContext;

    public CurrencyRatesStorage() {
        currencyData = new HashMap<>();
    }

    public CurrencyRatesStorage(Map<String, List<CurrencyRate>> currencyData) {
        this.currencyData = currencyData;
    }

    @PostConstruct
    public void postConstruct() {
        File folder = new File(servletContext.getRealPath("/WEB-INF/rates/"));
        List<CurrencyRate> rates;
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            currencyProvider = ProviderFactory.getProvider(FileUtils.getExtension(fileEntry));
            rates = Objects.requireNonNull(currencyProvider).getData(fileEntry);
            currencyData.put(FileUtils.stripExtension(fileEntry.getName()), rates);
        }
    }

    public Map<String, List<CurrencyRate>> getAllRates() {
        return currencyData;
    }

    public Map<String, CurrencyRate> getRatesForCode(String code) {
        Map<String, CurrencyRate> result = new HashMap<>();

        for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet())
            if (entry.getValue() == null)
                result.put(entry.getKey(), null);
            else
                for (CurrencyRate rate : entry.getValue()) {
                    if (rate.getCode().equals(code)) {
                        result.put(entry.getKey(), rate);
                        break;
                    }
                }
        return result;
    }

    public Map<String, Double> getBuyPriceForCode(String code) {
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet())
            for (CurrencyRate rate : entry.getValue()) {
                if (rate != null && rate.getCode().equals(code) && rate.getBuy() > 0) {
                    result.put(entry.getKey(), rate.getBuy());
                    break;
                }
            }
        return result;
    }

    public Map<String, Double> getSellPricesForCode(String code) {
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet())
            for (CurrencyRate rate : entry.getValue()) {
                if (rate != null && rate.getCode().equals(code) && rate.getSell() > 0) {
                    result.put(entry.getKey(), rate.getSell());
                    break;
                }
            }
        return result;
    }

    public Map<String, Map<String, Map.Entry<String, Double>>> getBestPropositions() {
        //{code, {buy:{bank, value},sell:{bank,value}}}
        Map<String, Map<String, Map.Entry<String, Double>>> result = new HashMap<>();
        Set<String> codes = getAllCodes();

        for (String code : codes) {
            Double maxBuy = Double.MIN_VALUE;
            String maxBuyBank = "";
            Double minSell = Double.MAX_VALUE;
            String minSellBank = "";
            for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet())
                for (CurrencyRate rate : entry.getValue()) {
                    if (rate.getCode().equals(code)) {

                        if (rate.getBuy() >= maxBuy) {
                            maxBuy = rate.getBuy();
                            maxBuyBank = entry.getKey();
                        }

                        if (rate.getSell() <= minSell) {
                            minSell = rate.getSell();
                            minSellBank = entry.getKey();
                        }
                    }
                }

            if (maxBuy.equals(Double.MIN_VALUE)) maxBuy = 0.;
            if (minSell.equals(Double.MAX_VALUE)) minSell = 0.;

            Map.Entry<String, Double> bankBuyEntry = new AbstractMap.SimpleEntry<>(maxBuyBank, maxBuy);
            Map.Entry<String, Double> bankSellEntry = new AbstractMap.SimpleEntry<>(minSellBank, minSell);
            Map<String, Map.Entry<String, Double>> mapEntry = new HashMap<>();
            mapEntry.put("buy", bankBuyEntry);
            mapEntry.put("sell", bankSellEntry);
            result.put(code, mapEntry);

        }
        return result;
    }

    private Set<String> getAllCodes() {
        Set<String> list = new HashSet<>();
        for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet())
            for (CurrencyRate rate : entry.getValue()) {
                list.add(rate.getCode());
            }
        return list;
    }


    public void updateSellPriceForBank(String bank, String code, String value) {
        updateSellPriceForBank(bank, code, value, "sell");
    }

    public void updateBuyPriceForBank(String bank, String code, String value) {
        updateSellPriceForBank(bank, code, value, "buy");
    }

    private void updateSellPriceForBank(String bank, String code, String value, String tag) {
        File file = FileUtils.findFileByName(servletContext, bank);
        currencyProvider = ProviderFactory.getProvider(FileUtils.getExtension(file));
        if (tag.equals("sell"))
            currencyProvider.updateSellPrice(file, code, Double.valueOf(value));
        else if (tag.equals("buy"))
            currencyProvider.updateBuyPrice(file, code, Double.valueOf(value));
        List<CurrencyRate> entry = currencyProvider.getData(file);
        currencyData.put(bank, entry);

    }


    public void deleteRatesForBank(String bank) {
        for (Map.Entry<String, List<CurrencyRate>> entry : currencyData.entrySet()) {
            if (entry.getKey().equals(bank))
                entry.getValue().clear();
        }
        File file = FileUtils.findFileByName(servletContext, bank);
        currencyProvider = ProviderFactory.getProvider(FileUtils.getExtension(file));
        currencyProvider.deleteRatesForBank(file);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

