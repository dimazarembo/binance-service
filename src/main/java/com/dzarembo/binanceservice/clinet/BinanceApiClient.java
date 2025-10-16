package com.dzarembo.binanceservice.clinet;

import com.dzarembo.binanceservice.model.FundingRate;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BinanceApiClient is responsible for interacting directly with the Binance Futures REST API.
 * It fetches funding rate data for all trading pairs from the official Binance endpoint.
 */
@Component
public class BinanceApiClient {
    private static final String BINANCE_URL = "https://fapi.binance.com/fapi/v1/premiumIndex";
    private final RestTemplate restTemplate = new RestTemplate();


    /**
     * Fetches all current funding rates from Binance Futures API.
     *
     * @return Map containing symbol and funding rate details.
     */
    public Map<String, FundingRate> fetchFundingRates() {
        try {
            FundingRateResponse[] response = restTemplate.getForObject(BINANCE_URL, FundingRateResponse[].class);
            if (response == null) return Collections.emptyMap();

            return Arrays.stream(response)
                    .collect(Collectors.toMap(
                            FundingRateResponse::getSymbol,
                            r -> new FundingRate(
                                    r.getSymbol(),
                                    Double.parseDouble(r.getLastFundingRate()),
                                    r.getNextFundingTime())));
        } catch (RestClientException e) {
            System.err.println("Error fetching funding rates: " + e.getMessage());
            return Collections.emptyMap();
        }

    }

    /**
     * DTO representing the funding rate information for a single Binance futures trading pair.
     */
    @Getter
    private static class FundingRateResponse {
        private String symbol;                      // e.g., BTCUSDT
        private String lastFundingRate;
        private long nextFundingTime;

    }
}
