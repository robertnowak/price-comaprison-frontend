package com.kodilla.prices.external.prices;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    public void addOffer(AmazonOfferDto amazonOfferDto){
        try {
            HttpClient.newHttpClient()
                    .send(createRequestForAddOffers(amazonOfferDto), HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            logger.error("Cannot add offers", e);
        }
    }

    private AmazonOfferDto[] getAmazonOfferDtos() throws IOException, InterruptedException {
        String body =
                HttpClient.newHttpClient()
                        .send(createRequestForgetOffers(), HttpResponse.BodyHandlers.ofString()).body();
        logger.info("response: {} ",body );
        return arrayReader.readValue(body, AmazonOfferDto[].class);
    }

    private HttpRequest createRequestForgetOffers() {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URIBuilder(offersUrl).setPath("/v1/amazon/getOffers/").build())
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
   private HttpRequest createRequestForAddOffers(AmazonOfferDto amazonOfferDto) {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URIBuilder(offersUrl).setPath("/v1/amazon")
                            .addParameter("id", amazonOfferDto.getAsin())
                            .addParameter("userId", "123")
                            .addParameter("targetPrice", amazonOfferDto.getTargetPrice().toString())
                            .build())
                    .method("POST", HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(amazonOfferDto)))
                    .build();
        } catch (URISyntaxException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
