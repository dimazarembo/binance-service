package com.dzarembo.binanceservice.controller;


import com.dzarembo.binanceservice.cache.FundingCache;
import com.dzarembo.binanceservice.model.FundingRate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
/**
 * REST controller exposing Binance funding rate data to external clients.
 */
@RestController
@RequestMapping("/api/binance/funding")
@RequiredArgsConstructor
public class FundingController {
    private final FundingCache fundingCache;

    /**
     * Returns funding rate data for all symbols currently stored in the cache.
     */
    @GetMapping
    public Collection<FundingRate> getAll() {
        return fundingCache.getAll();
    }

    /**
     * Returns funding rate for a specific symbol (e.g., BTCUSDT).
     */
    @GetMapping("/{symbol}")
    public FundingRate getBySymbol(@PathVariable String symbol) {
        return fundingCache.get(symbol.toUpperCase());
    }
}
