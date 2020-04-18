package com.questland.handbook.controller;

import com.questland.handbook.service.DailyBossQueryService;
import com.questland.handbook.model.DailyBoss;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DailyBossController {
  private final DailyBossQueryService dailyBossQueryService;

  @GetMapping("/dailyboss/current")
  public DailyBoss getCurrentDailyBoss() {
    DailyBoss currentDailyBoss = dailyBossQueryService.getCurrentDailyBoss();
    return currentDailyBoss != null
           ? currentDailyBoss
           : new DailyBoss("Couldn't find current daily boss");
  }

}
