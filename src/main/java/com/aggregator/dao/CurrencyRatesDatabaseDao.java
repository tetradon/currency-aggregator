package com.aggregator.dao;

import com.aggregator.exception.BankNotFoundException;
import com.aggregator.exception.CurrencyNotFoundException;
import com.aggregator.model.CurrencyRate;
import com.aggregator.utils.BestPropositionsUtils;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;
import javax.sql.DataSource;
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
        return BestPropositionsUtils.get(getAllRates());
    }
}
