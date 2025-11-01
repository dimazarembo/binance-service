package com.dzarembo.binanceservice.clinet;

import com.dzarembo.binanceservice.model.FundingRate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
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
     * Filters out pairs with fundingRate = 0.0 (inactive or delisted).
     */
    public Map<String, FundingRate> fetchFundingRates() {
        try {
            // 1️⃣ Получаем основные данные (ставка + время)
            var rates = restTemplate.getForObject(BASE_URL + "/fapi/v1/premiumIndex", FundingRateResponse[].class);
            if (rates == null) return Map.of();

            // 2️⃣ Получаем интервалы фандинга (если есть)
            var info = restTemplate.getForObject(BASE_URL + "/fapi/v1/fundingInfo", FundingInfoResponse[].class);
            Map<String, Integer> intervalMap = Optional.ofNullable(info)
                    .map(Arrays::asList)
                    .orElse(List.of())
                    .stream()
                    .collect(Collectors.toMap(
                            FundingInfoResponse::symbol,
                            FundingInfoResponse::fundingIntervalHours,
                            (a, b) -> a
                    ));

            // 3️⃣ Объединяем и фильтруем нулевые фандинги
            return Arrays.stream(rates)
                    .filter(r -> r.lastFundingRate() != null)
                    .map(r -> {
                        try {
                            double rate = Double.parseDouble(r.lastFundingRate());
                            if (rate == 0.0) return null; // пропускаем нулевые пары
                            return new FundingRate(
                                    r.symbol(),
                                    rate,
                                    r.nextFundingTime(),
                                    intervalMap.getOrDefault(r.symbol(), DEFAULT_INTERVAL)
                            );
                        } catch (Exception e) {
                            log.warn("Failed to parse funding rate for symbol {}: {}", r.symbol(), e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
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
