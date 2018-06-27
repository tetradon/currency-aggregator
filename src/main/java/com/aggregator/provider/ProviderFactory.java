package com.aggregator.provider;


public final class ProviderFactory {
    private static final  XmlCurrencyProvider XML_CURRENCY_PROVIDER =
            new XmlCurrencyProvider();
    private static final  JsonCurrencyProvider JSON_CURRENCY_PROVIDER =
            new JsonCurrencyProvider();
    private static final  CsvCurrencyProvider CSV_CURRENCY_PROVIDER =
            new CsvCurrencyProvider();

    private ProviderFactory() {
    }

    public static CurrencyProvider getProvider(final String extension) {
        switch (extension) {
            case "xml":
                return XML_CURRENCY_PROVIDER;
            case "json":
                return JSON_CURRENCY_PROVIDER;
            case "csv":
                return CSV_CURRENCY_PROVIDER;
            default:
                throw new IllegalArgumentException(extension
                        + " is not supported");
        }
    }
}
