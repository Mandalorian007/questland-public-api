package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.BossStats;
import com.questland.handbook.publicmodel.RedGuildBossStrikerRequest;
import com.questland.handbook.publicmodel.StrikerCalculatorResponse;
import com.questland.handbook.service.BossDataService;
import com.questland.handbook.service.RedStrikerCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigInteger;
import java.util.Map;

@ApiIgnore
@RestController
@RequiredArgsConstructor
@RequestMapping("/calc")
public class StrikerCalculatorEndpoints {

    private final RedStrikerCalculatorService redStrikerCalculatorService;
    private final BossDataService bossDataService;

    @PostMapping("/red-guild-boss")
    public StrikerCalculatorResponse test(@RequestBody RedGuildBossStrikerRequest strikerCalculatorRequest) {
        Map<BigInteger, BossStats> guildBossStatsMap = bossDataService.getGuildBossStats();
        BossStats guildBossStats = guildBossStatsMap.get(BigInteger.valueOf(strikerCalculatorRequest.getBossLevel()));
        double riskyThreshold = redStrikerCalculatorService.riskyFormula(
                strikerCalculatorRequest.getHeroHealth(),
                guildBossStats.getMagic().intValue());
        double stableThreshold = redStrikerCalculatorService.stableFormula(
                strikerCalculatorRequest.getHeroHealth(),
                strikerCalculatorRequest.getHeroMagic(),
                strikerCalculatorRequest.getHeroTranscendentalTornadoLevel(),
                guildBossStats.getMagic().doubleValue(),
                riskyThreshold);
        double soloKillThreshold = redStrikerCalculatorService.soloKillFormula(
                strikerCalculatorRequest.getHeroAttack(),
                strikerCalculatorRequest.getHeroDefense(),
                strikerCalculatorRequest.getHeroBloodlustLevel(),
                strikerCalculatorRequest.getHeroRunicTouchLevel(),
                stableThreshold,
                guildBossStats.getHealth().doubleValue(),
                guildBossStats.getDefense().doubleValue(),
                guildBossStats.getMagic().doubleValue());
        return StrikerCalculatorResponse.builder()
                .riskyThreshold(ceilHalf(riskyThreshold))
                .stableThreshold(ceilHalf(stableThreshold))
                .soloKillThreshold(ceilHalf(soloKillThreshold))
                .build();
    }

    private static double ceilHalf(double num) {
        if((num - (int) num) > .5) {
            return Math.ceil(num);
        } else {
            return Math.floor(num) + .5;
        }
    }
}
