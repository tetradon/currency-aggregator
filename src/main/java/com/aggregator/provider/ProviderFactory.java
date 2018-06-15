package com.aggregator.provider;


public class ProviderFactory {
    private final static XmlCurrencyProvider xmlCurrencyProvider = new XmlCurrencyProvider();
    private final static JsonCurrencyProvider jsonCurrencyProvider = new JsonCurrencyProvider();
    private final static CsvCurrencyProvider csvCurrencyProvider = new CsvCurrencyProvider();

    private ProviderFactory() {
    }

    public static CurrencyProvider getProvider(String extension) {
        switch (extension) {
            case "xml":
                return xmlCurrencyProvider;
            case "json":
                return jsonCurrencyProvider;
            case "csv":
                return csvCurrencyProvider;
        }
        return null;
    }

}
