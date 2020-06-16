package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.MonsterSlayerScore;
import com.questland.handbook.service.MonsterSlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MonsterSlayerEndpoints {

  private final MonsterSlayerService monsterSlayerService;

  @GetMapping("/monster-slayer")
  public MonsterSlayerScore getMonsterSlayerScore() {
    return monsterSlayerService.getStageScores();
  }
}