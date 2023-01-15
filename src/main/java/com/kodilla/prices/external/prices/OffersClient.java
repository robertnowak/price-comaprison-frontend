package com.kodilla.prices.external.prices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.kodilla.prices.domain.offer.AmazonOffer;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class OffersClient {

    private final Logger logger = LoggerFactory.getLogger(OffersClient.class);

    private final ObjectMapper objectMapper;
    private final ObjectReader arrayReader;

    @Value("${offers.client.url}")
    private String offersUrl;

    public OffersClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.arrayReader = objectMapper.readerForArrayOf(AmazonOfferDto.class);
    }

    public List<AmazonOffer> getOffers() {
        try {
            AmazonOfferDto[] productsArrayNode = getAmazonOfferDtos();
            return Arrays.stream(productsArrayNode).map(AmazonOfferDto::toDomain).toList();
        } catch (Exception e) {
            logger.error("Cannot read offers", e);
            return Collections.emptyList();
        }
    }

    private AmazonOfferDto[] getAmazonOfferDtos() throws IOException, InterruptedException {
        String body =
                HttpClient.newHttpClient()
                        .send(createRequestForOffers(), HttpResponse.BodyHandlers.ofString()).body();
        logger.info("response: {} ",body );
        return arrayReader.readValue(body, AmazonOfferDto[].class);
    }

    private HttpRequest createRequestForOffers() {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URIBuilder(offersUrl).setPath("/v1/amazon/getOffers/").build())
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
