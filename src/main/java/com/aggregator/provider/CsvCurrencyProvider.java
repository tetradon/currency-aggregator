package com.aggregator.provider;

import com.aggregator.model.CurrencyRate;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.aggregator.utils.FIleUtils.deleteContentOfFile;

public class CsvCurrencyProvider implements CurrencyProvider {
    private static List<CurrencyRate> resultList = new ArrayList<>();

    @Override
    public List<CurrencyRate> getData(File file) {
        ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
        strategy.setType(CurrencyRate.class);
        strategy.setColumnMapping("code", "buy", "sell");
        CsvToBean csv = new CsvToBean();
        try {
            resultList = csv.parse(strategy, new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public void updateBuyPrice(File file, String code, Double newValue) {
        updatePrice(file, code, newValue, "buy");
    }

    @Override
    public void updateSellPrice(File file, String code, Double newValue) {
        updatePrice(file, code, newValue, "sell");
    }

    @Override
    public void deleteRatesForBank(File file) {
        deleteContentOfFile(file);
    }

    private void updatePrice(File file, String code, Double newValue, String tag) {
        List<String[]> entries = null;
        String[] rowForDeleting = new String[1];
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            entries = reader.readAll();
            for (String[] row : entries) {
                if(row[0].equals(code)) {
                    rowForDeleting = row;
                }
            }
            entries.remove(rowForDeleting);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter(file),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER)){
            if(tag.equals("buy")) {
                String[] newEntry = (code + "#" + newValue + "#" + rowForDeleting[2]).split("#");
                Objects.requireNonNull(entries).add(newEntry);
                writer.writeAll(entries);
            }
            if(tag.equals("sell")) {
                String[] newEntry = (code + "#" + rowForDeleting[1]+ "#" +newValue).split("#");
                Objects.requireNonNull(entries).add(newEntry);
                writer.writeAll(entries);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
