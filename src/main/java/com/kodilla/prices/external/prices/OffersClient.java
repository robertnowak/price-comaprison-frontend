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
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
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

    public void addOffer(AmazonOfferDto amazonOfferDto) {
        send(
                baseUrl().queryParam("id", amazonOfferDto.getAsin())
                        .queryParam("userId", "123") //logged user id
                        .queryParam("targetPrice", amazonOfferDto.getTargetPrice().toString()),
                "POST",
                jsonBodyPublisher(amazonOfferDto),
                HttpResponse.BodyHandlers.discarding()
        );
    }

    private HttpRequest.BodyPublisher jsonBodyPublisher(Object dto) {
        try {
            return HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            throw new RestClientException("error performing json conversion of " + dto, e);
        }
    }

    private AmazonOfferDto[] getAmazonOfferDtos() throws IOException, InterruptedException {
        String body =
                HttpClient.newHttpClient()
                        .send(createRequestForgetOffers(), HttpResponse.BodyHandlers.ofString()).body();
        logger.info("response: {} ", body);
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

    public void delete(String offerId) {
        send(baseUrl().pathSegment("deleteOffer", offerId), "DELETE");
    }

    public void refresh(String asin) {
        send(baseUrl().pathSegment("refreshPrice", asin), "PATCH");
    }

    private Void send(UriComponentsBuilder uriComponentsBuilder, String method, HttpRequest.BodyPublisher bodyPublisher) {
        return send(uriComponentsBuilder, method, bodyPublisher, HttpResponse.BodyHandlers.discarding());
    }


    private Void send(UriComponentsBuilder uriComponentsBuilder, String method) {
        return send(uriComponentsBuilder, method, HttpRequest.BodyPublishers.noBody(), HttpResponse.BodyHandlers.discarding());
    }


    private <T> T send(UriComponentsBuilder uriComponentsBuilder, String method, HttpRequest.BodyPublisher bodyPublisher, HttpResponse.BodyHandler<T> bodyHandler) {
        URI uri = uriComponentsBuilder.build().toUri();
        try {
            T responseBody = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder(uri)
                                    .method(method, bodyPublisher)
                                    .build(),
                            bodyHandler)
                    .body();

            logger.info("Sending request to {} , ended with {}", uri, responseBody);
            return responseBody;
        } catch (Exception e) {
            throw new RestClientException("error performing request", e);
        }
    }

    private UriComponentsBuilder baseUrl() {
        return UriComponentsBuilder.fromHttpUrl(offersUrl).path("/v1/amazon");
    }
}
