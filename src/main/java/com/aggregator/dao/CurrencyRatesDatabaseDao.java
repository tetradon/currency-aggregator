package com.aggregator.dao;

import com.aggregator.exception.BankNotFoundException;
import com.aggregator.exception.CurrencyNotFoundException;
import com.aggregator.model.CurrencyRate;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;
import javax.sql.DataSource;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("db")
public final class CurrencyRatesDatabaseDao implements CurrencyRatesDao {

    private JdbcTemplate jdbcTemplate;
    private static final String SELECT_ALL = "SELECT * FROM currency_rates";

    @Autowired
    public CurrencyRatesDatabaseDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Map<String, List<CurrencyRate>> getAllRates() {

        Map<String, List<CurrencyRate>> resultMap = new HashMap<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(SELECT_ALL);

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

    private Map<String, MonetaryAmount> getPricesForCode(String code,
                                                         String operation) {
        String sql = "SELECT bank,${column} FROM currency_rates WHERE code = ?"
                .replace("${column}", operation);
        return jdbcTemplate.query(
                sql,
                rs -> {
                    Map<String, MonetaryAmount> innerMap = new HashMap<>();
                    while (rs.next()) {
                        String bank = rs.getString("bank");
                        MonetaryAmount money =
                                Money.of(rs.getDouble(operation), code);
                        innerMap.put(bank, money);
                    }
                    return innerMap;
                },
                code);
    }

    @Override
    public void updateSellPriceForBank(String bank,
                                       String code, String value) {
        String sql = "UPDATE currency_rates SET sell = ? "
                     + "WHERE bank = ? AND code = ?";

        throwExceptionIfWrongCode(code);
        throwExceptionIfWrongBank(bank);

        jdbcTemplate.update(sql, value, bank, code);

    }

    private void throwExceptionIfWrongBank(String bank) {

        List<Map<String, Object>> checkBank
                = jdbcTemplate.queryForList(SELECT_ALL
                                            + " WHERE bank = ?", bank);
        if (checkBank.isEmpty()) {
            throw new BankNotFoundException(bank);
        }
    }

    private void throwExceptionIfWrongCode(String code) {
        List<Map<String, Object>> checkCode
                = jdbcTemplate.queryForList(SELECT_ALL
                                            + " WHERE code = ?", code);
        if (checkCode.isEmpty()) {
            throw new CurrencyNotFoundException(code);
        }
    }

    @Override
    public void updateBuyPriceForBank(String bank,
                                      String code, String value) {
        String sql = "UPDATE currency_rates SET buy = ? "
                     + "WHERE bank = ? AND code = ?";

        throwExceptionIfWrongCode(code);
        throwExceptionIfWrongBank(bank);

        jdbcTemplate.update(sql, value, bank, code);
    }

    @Override
    public void deleteRatesForBank(String bank) {
        String sql = "DELETE FROM currency_rates WHERE bank = ?";

        throwExceptionIfWrongBank(bank);

        jdbcTemplate.update(sql, bank);
    }

    @Override
    public Map<String,
            Map<String,
                    Map.Entry<String, MonetaryAmount>>> getBestPropositions() {
        String sql = ""
                     + "SELECT\n"
                     + "  max_buy_result.code,\n"
                     + "  max_buy_result.bank  AS max_buy_bank,\n"
                     + "  max_buy,\n"
                     + "  min_sell_result.bank AS min_sell_bank,\n"
                     + "  min_sell\n"
                     + "FROM (SELECT\n"
                     + "        max_buy_select.code,\n"
                     + "        bank,\n"
                     + "        max_buy\n"
                     + "      FROM currency_rates AS all_banks\n"
                     + "        INNER JOIN (\n"
                     + "                     SELECT\n"
                     + "                       code,\n"
                     + "                       MAX(buy) AS max_buy\n"
                     + "                     FROM currency_rates\n"
                     + "                     GROUP BY code\n"
                     + "                   ) max_buy_select\n"
                     + "          ON all_banks.code = max_buy_select.code\n"
                     + "             AND all_banks.buy = max_buy)"
                     + "                AS max_buy_result\n"
                     + "  INNER JOIN (\n"
                     + "               SELECT\n"
                     + "                 min_sell_select.code,\n"
                     + "                 bank,\n"
                     + "                 min_sell\n"
                     + "               FROM currency_rates AS all_banks\n"
                     + "                 INNER JOIN (\n"
                     + "                              SELECT\n"
                     + "                                code,\n"
                     + "                                MIN(sell) AS min_sell\n"
                     + "                              FROM currency_rates\n"
                     + "                              GROUP BY code\n"
                     + "                            ) AS min_sell_select\n"
                     + "                 ON all_banks.code "
                     + "                        = min_sell_select.code\n"
                     + "                      AND all_banks.sell = min_sell\n"
                     + "             ) min_sell_result\n"
                     + "    ON max_buy_result.code = min_sell_result.code\n";

        Map<String, Map<String, Map.Entry<String, MonetaryAmount>>> result
                = new HashMap<>();

        return jdbcTemplate.query(
                sql,
                rs -> {
                    while (rs.next()) {
                        Map<String, Map.Entry<String, MonetaryAmount>> mapEntry
                                = new HashMap<>();
                        String code = rs.getString("code");

                        String maxBuyBank = rs.getString("max_buy_bank");
                        MonetaryAmount maxBuy =
                                Money.of(rs.getDouble("max_buy"), code);
                        Map.Entry<String, MonetaryAmount> bankBuyEntry =
                                new AbstractMap
                                        .SimpleEntry<>(maxBuyBank, maxBuy);
                        mapEntry.put("buy", bankBuyEntry);

                        String minSellBank = rs.getString("min_sell_bank");
                        MonetaryAmount minSell =
                                Money.of(rs.getDouble("min_sell"), code);
                        Map.Entry<String, MonetaryAmount> bankSellEntry =
                                new AbstractMap
                                        .SimpleEntry<>(minSellBank, minSell);
                        mapEntry.put("sell", bankSellEntry);

                        result.put(code, mapEntry);
                    }
                    return result;
                });
    }
}
