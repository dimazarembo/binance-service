package com.dzarembo.binanceservice.cache;

import com.dzarembo.binanceservice.model.FundingRate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * In-memory cache that stores the most recent Binance funding rate data.
 * The cache is periodically updated by the BinanceUpdater.
 */
@Component
public class FundingCache {
    private final Map<String, FundingRate> cache = new ConcurrentHashMap<>();

    /**
     * Stores all funding rates retrieved from Binance.
     *
     * @param data the map of rates to be cached
     */
    public void putAll(Map<String, FundingRate> data) {
        cache.clear();
        cache.putAll(data);
    }


    /**
     * Returns the funding rate for a specific symbol if present.
     */
    public FundingRate get(String symbol) {
        return cache.get(symbol);
    }

    /**
     * Returns all cached funding rates.
     */
    public Collection<FundingRate> getAll() {
        return cache.values();
    }
}
