package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.MonsterSlayerScore;
import com.questland.handbook.service.MonsterSlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequiredArgsConstructor
public class MonsterSlayerEndpoints {

  private final MonsterSlayerService monsterSlayerService;

  private MonsterSlayerScore monsterSlayerScore;

  @GetMapping("/monster-slayer")
  public MonsterSlayerScore getMonsterSlayerScore() {
    if (monsterSlayerScore == null) {
      monsterSlayerScore = monsterSlayerService.getStageScores();
    }
    //TODO consider if monster slayer is inactive
    return monsterSlayerScore;
  }
}
