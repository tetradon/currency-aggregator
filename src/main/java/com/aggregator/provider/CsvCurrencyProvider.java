package com.aggregator.provider;

import com.aggregator.model.CurrencyRate;
import com.aggregator.utils.FileUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CsvCurrencyProvider implements CurrencyProvider {
    private List<CurrencyRate> resultList = new ArrayList<>();

    @Override
    public List<CurrencyRate> getData(final File file) {
        try {
            resultList
                    = new CsvToBeanBuilder<CurrencyRate>(
                            new InputStreamReader(new FileInputStream(file),
                                    StandardCharsets.UTF_8))
                    .withType(CurrencyRate.class)
                    .build()
                    .parse();
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
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8))) {
            entries = reader.readAll();
            for (String[] row : entries) {
                if (row[0].equals(code)) {
                    rowForDeleting = row;
                }
            }
            entries.remove(rowForDeleting);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (CSVWriter writer = new CSVWriter(
                new OutputStreamWriter(
                        new FileOutputStream(file), StandardCharsets.UTF_8),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {
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
