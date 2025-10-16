package com.dzarembo.binanceservice.scheduler;

import com.dzarembo.binanceservice.cache.FundingCache;
import com.dzarembo.binanceservice.clinet.BinanceApiClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled component responsible for periodically refreshing the Binance funding rate cache.
 */
@Component
@RequiredArgsConstructor
public class BinanceUpdater {
    private static final Logger log = LoggerFactory.getLogger(BinanceUpdater.class);
    private final FundingCache cache;
    private final BinanceApiClient binanceApiClient;

    /**
     * Fetches funding rates from Binance every fixed interval and updates the cache.
     * The update interval is configured using @Scheduled.
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) //5 min
    public void updateFundingRates() {
        log.info("Updating funding rates...");
        cache.putAll(binanceApiClient.fetchFundingRates());
        log.info("Cache updated. Cached pairs: {}", cache.getAll().size());
    }
}
