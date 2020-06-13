package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.BattleLocation;
import com.questland.handbook.service.BattleLocationsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BattleLocationEndpoints {

  private final BattleLocationsService battleLocationsService;

  @GetMapping("/battle-locations")
  public List<BattleLocation> getBattleLocations() {
    return battleLocationsService.getBattleLocationDetails();
  }
}
