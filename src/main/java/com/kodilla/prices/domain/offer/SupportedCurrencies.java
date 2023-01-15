package com.kodilla.prices.domain.offer;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Set;

public class SupportedCurrencies {
    public static Set<CurrencyUnit> CURRENCIES = Set.of(
            Monetary.getCurrency("PLN"),
            Monetary.getCurrency("EUR"),
            Monetary.getCurrency("USD"));
}
