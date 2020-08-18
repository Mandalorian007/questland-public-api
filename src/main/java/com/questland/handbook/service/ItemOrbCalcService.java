package com.questland.handbook.service;

import org.springframework.stereotype.Service;

@Service
public class ItemOrbCalcService {

  public double calculateOrbStats(int baseStatValue, int potential, int enhance, int level) {
    double enhancedBaseStat = baseStatValue + (baseStatValue * (.05 * enhance));
    double enhancedPotential = potential + (potential * (.05 * enhance));
    return enhancedBaseStat + (enhancedPotential * (level - 1));
  }

}
