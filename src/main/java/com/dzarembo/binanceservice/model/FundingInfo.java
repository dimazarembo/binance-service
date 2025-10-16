package com.dzarembo.binanceservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundingInfo {
    private String symbol;
    private double markPrice;
    private double indexPrice;
    private double lastFundingRate;
    private double interestRate;
    private long nextFundingTime;
    private long time;
}