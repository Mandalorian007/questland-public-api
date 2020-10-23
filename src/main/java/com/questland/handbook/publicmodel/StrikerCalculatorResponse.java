package com.questland.handbook.publicmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StrikerCalculatorResponse {
    private double riskyThreshold;
    private double stableThreshold;
    private double soloKillThreshold;
}
