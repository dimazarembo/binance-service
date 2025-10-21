package com.dzarembo.binanceservice.clinet;

import com.dzarembo.binanceservice.model.FundingRate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handles communication with Binance Futures REST API.
 * Fetches funding rate data and funding interval configuration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BinanceApiClient {

    private static final String BASE_URL = "https://fapi.binance.com";
    private final RestTemplate restTemplate = new RestTemplate();

    private static final int DEFAULT_INTERVAL = 8; // default Binance interval (hours)

    /**
     * Fetches all current funding rates (premiumIndex endpoint)
     * and combines them with funding interval info (fundingInfo endpoint).
     */
    public Map<String, FundingRate> fetchFundingRates() {
        try {
            // 1️⃣ Fetch base funding data (rate + nextFundingTime)
            var rates = restTemplate.getForObject(BASE_URL + "/fapi/v1/premiumIndex", FundingRateResponse[].class);
            if (rates == null) return Map.of();

            // 2️⃣ Fetch fundingInfo (interval adjustments)
            var info = restTemplate.getForObject(BASE_URL + "/fapi/v1/fundingInfo", FundingInfoResponse[].class);
            Map<String, Integer> intervalMap = Optional.ofNullable(info)
                    .map(Arrays::asList)
                    .orElse(List.of())
                    .stream()
                    .collect(Collectors.toMap(FundingInfoResponse::symbol, FundingInfoResponse::fundingIntervalHours));

            // 3️⃣ Combine results
            return Arrays.stream(rates)
                    .filter(r -> r.lastFundingRate() != null)
                    .map(r -> new FundingRate(
                            r.symbol(),
                            Double.parseDouble(r.lastFundingRate()),
                            r.nextFundingTime(),
                            intervalMap.getOrDefault(r.symbol(), DEFAULT_INTERVAL)
                    ))
                    .collect(Collectors.toMap(FundingRate::getSymbol, Function.identity()));

        } catch (Exception e) {
            log.error("Error fetching Binance funding rates: {}", e.getMessage());
            return Map.of();
        }
    }

    // === Internal DTOs for Binance responses ===

    private record FundingRateResponse(String symbol, String lastFundingRate, long nextFundingTime) {
    }

    private record FundingInfoResponse(String symbol, Integer fundingIntervalHours) {
    }
}
