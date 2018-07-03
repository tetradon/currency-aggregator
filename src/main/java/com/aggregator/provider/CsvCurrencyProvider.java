package com.aggregator.provider;

import com.aggregator.model.CurrencyRate;
import com.aggregator.utils.FileUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CsvCurrencyProvider implements CurrencyProvider {
    private static final Logger log =
            LogManager.getLogger(CsvCurrencyProvider.class);

    @Override
    public List<CurrencyRate> getData(final File file) {
        List<CurrencyRate> resultList = new ArrayList<>();
        try (CSVReader reader =
                     new CSVReaderBuilder(
                             new InputStreamReader(
                                     new FileInputStream(file),
                                     StandardCharsets.UTF_8))
                .withSkipLines(1)
                             .build()) {
            List<String[]> entries = reader.readAll();
            for (String[] arr
                    : entries) {
                resultList.add(new CurrencyRate(
                        arr[0],
                        Double.parseDouble(arr[1]),
                        Double.parseDouble(arr[2])));
            }
        } catch (IOException e) {
            log.error("Exception while creating CSVReader", e);
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
           log.error("Exception while creating CSVReader", e);
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
            log.error("Exception while creating CSVWriter", e);
        }
    }
}
