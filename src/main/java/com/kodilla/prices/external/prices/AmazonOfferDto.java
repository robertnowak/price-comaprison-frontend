package com.kodilla.prices.external.prices;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kodilla.prices.domain.offer.AmazonOffer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javamoney.moneta.FastMoney;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AmazonOfferDto {

    private String id;
    @JsonProperty("asin")
    private String asin;
    @JsonProperty("product_name")
    private String title;
    @JsonProperty("current_price")
    private BigDecimal currentPrice;
    @JsonProperty("locale")
    private String locale;

    @JsonProperty("currency_symbol")
    private String currency_symbol;

    private BigDecimal targetPrice;


    public AmazonOffer toDomain(){
        return new AmazonOffer(
                id,
                asin,
                title,
                FastMoney.of(currentPrice, "USD"),
                targetPrice != null ? FastMoney.of(targetPrice, "PLN") : null
        );
    }

}
