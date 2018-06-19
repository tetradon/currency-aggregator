package com.aggregator.provider;

import com.aggregator.model.CurrencyRate;
import com.aggregator.utils.FileUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CsvCurrencyProvider implements CurrencyProvider {
    private static List<CurrencyRate> resultList = new ArrayList<>();

    @Override
    public List<CurrencyRate> getData(final File file) {
        ColumnPositionMappingStrategy strategy =
                new ColumnPositionMappingStrategy();
        strategy.setType(CurrencyRate.class);
        strategy.setColumnMapping("currencyRateCode",
                "currencyRateBuyPrice", "currencyRateSellPrice");
        CsvToBean csv = new CsvToBean();
        try {
            resultList = csv.parse(strategy, new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public void updateBuyPrice(final File file, final String code,
                               final Double newValue) {
        updatePrice(file, code, newValue, "buy");
    }

    @Override
    public void updateSellPrice(final File file, final String code,
                                final Double newValue) {
        updatePrice(file, code, newValue, "sell");
    }

    @Override
    public void deleteRatesForBank(final File file) {
        FileUtils.deleteContentOfFile(file);
    }

    private void updatePrice(final File file, final String code,
                             final Double newValue, final String tag) {
        List<String[]> entries = null;
        String[] rowForDeleting = new String[1];
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            entries = reader.readAll();
            for (String[] row : entries) {
                if (row[0].equals(code)) {
                    rowForDeleting = row;
                }
            }
            entries.remove(rowForDeleting);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter(file),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER)) {
            if (tag.equals("buy")) {
                String[] newEntry = (code + "#" + newValue + "#"
                        + rowForDeleting[2]).split("#");
                Objects.requireNonNull(entries).add(newEntry);
                writer.writeAll(entries);
            }
            if (tag.equals("sell")) {
                String[] newEntry = (code + "#" + rowForDeleting[1]
                        + "#" + newValue).split("#");
                Objects.requireNonNull(entries).add(newEntry);
                writer.writeAll(entries);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
