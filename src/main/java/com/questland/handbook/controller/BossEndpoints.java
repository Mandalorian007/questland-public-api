package com.questland.handbook.controller;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.DailyBoss;
import com.questland.handbook.service.DailyBossQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BossEndpoints {
    private final DailyBossQueryService dailyBossQueryService;

    @GetMapping("/dailyboss/current")
    public DailyBoss getCurrentDailyBoss(
            @RequestParam(value = "server", defaultValue = "GLOBAL") QuestlandServer server) {
        DailyBoss currentDailyBoss = dailyBossQueryService.getCurrentDailyBoss(server);
        return currentDailyBoss != null
                ? currentDailyBoss
                : new DailyBoss("Couldn't find current daily boss");
    }
}
