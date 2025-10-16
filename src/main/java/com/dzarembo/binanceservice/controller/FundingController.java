package com.dzarembo.binanceservice.controller;


import com.dzarembo.binanceservice.model.FundingInfo;
import com.dzarembo.binanceservice.service.FundingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/binance/funding")
public class FundingController {

    private final FundingService fundingService;

    public FundingController(FundingService fundingService) {
        this.fundingService = fundingService;
    }


    @GetMapping("/{symbol}")
    public FundingInfo getFundingRate(@PathVariable String symbol) {
        return fundingService.getLatestFundingRate(symbol.toUpperCase());
    }

    @PostMapping("/batch")
    public List<FundingInfo> getFundingRates(@RequestBody List<String> symbols) {
        return fundingService.getAllFundingRates(symbols);
    }
}
