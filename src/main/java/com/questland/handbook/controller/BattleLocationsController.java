package com.questland.handbook.controller;

import com.questland.handbook.service.BattleLocationsService;
import com.questland.handbook.model.BattleLocation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BattleLocationsController {

  private final BattleLocationsService battleLocationsService;

  private List<BattleLocation> battleLocationsCache;

  @GetMapping("/battle-locations")
  public List<BattleLocation> getBattleLocations() {
    if (battleLocationsCache == null) {
      battleLocationsCache = battleLocationsService.getBattleLocationDetails();
    }
    return battleLocationsCache;
  }
}
