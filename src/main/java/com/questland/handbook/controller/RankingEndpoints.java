package com.questland.handbook.controller;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.GuildRanking;
import com.questland.handbook.publicmodel.HallOfFame;
import com.questland.handbook.service.GuildRankingService;
import com.questland.handbook.service.HallOfFameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RankingEndpoints {

    private final HallOfFameService hallOfFameService;
    private final GuildRankingService guildRankingService;

    @GetMapping("/halloffame")
    public HallOfFame getHallOfFame(@RequestParam("server") String server) {
        return hallOfFameService.getHallOfFame(QuestlandServer.valueOf(server));
    }

    @GetMapping("/guildranking")
    public GuildRanking getGuildRanking(@RequestParam("server") String server) {
        return guildRankingService.getGuildRanking(QuestlandServer.valueOf(server));
    }
}
