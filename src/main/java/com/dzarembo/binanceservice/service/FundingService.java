package com.dzarembo.binanceservice.service;

import com.dzarembo.binanceservice.model.FundingInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class FundingService {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "https://fapi.binance.com/fapi/v1/premiumIndex";

    public List<FundingInfo> getAllFundingRates(List<String> symbols) {
        return symbols.stream().map(this::getLatestFundingRate).toList();
    }

    public FundingInfo getLatestFundingRate(String symbol) {

        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("symbol", symbol)
                .toUriString();

        return restTemplate.getForObject(url, FundingInfo.class);
    }
}
