package com.aggregator.dao;

import com.aggregator.model.CurrencyRate;
import com.aggregator.utils.ListUtils;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;
import javax.sql.DataSource;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public final class CurrencyRatesDaoImpl implements CurrencyRatesDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CurrencyRatesDaoImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Map<String, List<CurrencyRate>> getAllRates() {

        Map<String, List<CurrencyRate>> resultMap = new HashMap<>();
        String sql = "SELECT * FROM currency_rates";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        String previousBank = (String) rows.get(0).get("bank");
        resultMap.put(previousBank, new ArrayList<>());
        for (Map row : rows) {
            String code = (String) row.get("code");
            Double buy = (Double) row.get("buy");
            Double sell = (Double) row.get("sell");
            String currentRowBank = (String) row.get("bank");
            if (!previousBank.equals(currentRowBank)) {
                ArrayList<CurrencyRate> list = new ArrayList<>();
                list.add(new CurrencyRate(code, buy, sell));
                resultMap.put(currentRowBank, list);
            } else {
                resultMap.get(currentRowBank)
                        .add(new CurrencyRate(code, buy, sell));
            }
            previousBank = currentRowBank;
        }
        return resultMap;
    }

    @Override
    public Map<String, CurrencyRate> getRatesForCode(String code) {
        String sql = "SELECT * FROM currency_rates WHERE code = ?";
        return jdbcTemplate.query(
                sql,
                rs -> {
                    Map<String, CurrencyRate> result = new HashMap<>();
                    while (rs.next()) {
                        String bank = rs.getString("bank");
                        CurrencyRate rate = new CurrencyRate(code,
                                rs.getDouble("buy"),
                                rs.getDouble("sell"));
                        result.put(bank, rate);
                    }
                    return result;
                },
                code);
    }

    @Override
    public Map<String, MonetaryAmount> getBuyPricesForCode(String code) {
        return getPricesForCode(code, "buy");
    }

    @Override
    public Map<String, MonetaryAmount> getSellPricesForCode(String code) {
        return getPricesForCode(code, "sell");
    }

    private Map<String, MonetaryAmount> getPricesForCode(String code, String operation) {
        String sql = "SELECT bank, " + operation + " FROM currency_rates WHERE code = ?";
        return jdbcTemplate.query(
                sql,
                rs -> {
                    Map<String, MonetaryAmount> innerMap = new HashMap<>();
                    while (rs.next()) {
                        String bank = rs.getString("bank");
                        MonetaryAmount money = Money.of(rs.getDouble(operation), code);
                        innerMap.put(bank, money);
                    }
                    return innerMap;
                },
                code);
    }

    @Override
    public void updateSellPriceForBank(String bank, String code, String value) {
        String sql = "UPDATE currency_rates SET sell = ? WHERE bank = ? AND code = ?";
        jdbcTemplate.update(sql, value, bank, code);
    }

    @Override
    public void updateBuyPriceForBank(String bank, String code, String value) {
        String sql = "UPDATE currency_rates SET buy = ? WHERE bank = ? AND code = ?";
        jdbcTemplate.update(sql, value, bank, code);
    }

    @Override
    public void deleteRatesForBank(String bank) {
        String sql = "DELETE FROM currency_rates WHERE bank = ?";
        jdbcTemplate.update(sql, bank);
    }

    @Override
    public Map<String, Map<String, Map.Entry<String, MonetaryAmount>>> getBestPropositions() {
        Map<String, Map<String, Map.Entry<String, MonetaryAmount>>> result
                = new HashMap<>();
        Set<String> codes = getAllCodes();

        for (String code : codes) {
            MonetaryAmount maxBuy = Money.of(Double.MIN_VALUE, code);
            String maxBuyBank = "";
            MonetaryAmount minSell = Money.of(Double.MAX_VALUE, code);
            String minSellBank = "";
            for (Map.Entry<String, List<CurrencyRate>> entry
                    : getAllRates().entrySet()) {

                List<CurrencyRate> rates = ListUtils
                        .filterOutByCode(code, entry);

                if (rates.isEmpty()) {
                    continue;
                }

                List<CurrencyRate> sortedBuyRates =
                        ListUtils.sortByBuyPrice(rates);
                List<CurrencyRate> sortedSellRates =
                        ListUtils.sortBySellPrice(rates);
                MonetaryAmount localMaxBuy =
                        ListUtils.getLast(sortedBuyRates);
                MonetaryAmount localMinSell =
                        ListUtils.getFirst(sortedSellRates);

                if (localMaxBuy.isGreaterThan(maxBuy)) {
                    maxBuy = localMaxBuy;
                    maxBuyBank = entry.getKey();
                }

                if (localMinSell.isLessThan(minSell)) {
                    minSell = localMinSell;
                    minSellBank = entry.getKey();
                }
            }

            Map.Entry<String, MonetaryAmount> bankBuyEntry =
                    new AbstractMap.SimpleEntry<>(maxBuyBank, maxBuy);
            Map.Entry<String, MonetaryAmount> bankSellEntry =
                    new AbstractMap.SimpleEntry<>(minSellBank, minSell);
            Map<String, Map.Entry<String, MonetaryAmount>> mapEntry
                    = new HashMap<>();
            mapEntry.put("buy", bankBuyEntry);
            mapEntry.put("sell", bankSellEntry);
            result.put(code, mapEntry);
        }
        return result;
    }

    private Set<String> getAllCodes() {
        Set<String> list = new HashSet<>();
        for (Map.Entry<String, List<CurrencyRate>> entry
                : getAllRates().entrySet()) {
            for (CurrencyRate rate : entry.getValue()) {
                list.add(rate.getCurrencyRateCode());
            }
        }
        return list;
    }
}
