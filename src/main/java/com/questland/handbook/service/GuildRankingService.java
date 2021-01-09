package com.questland.handbook.service;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.GuildRanking;
import com.questland.handbook.publicmodel.RankedGuild;
import com.questland.handbook.service.model.guildranking.GuildRankingWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GuildRankingService {
    private final GuildService guildService;

    private RestTemplate restTemplate = new RestTemplate();

    @Resource()
    Map<QuestlandServer, String> playerTokenMap;

    @Resource()
    Map<QuestlandServer, String> regionWorkerMap;

    @Cacheable(value = "guild-ranking", key = "#server.name()")
    public GuildRanking getGuildRanking(QuestlandServer server) {
        System.out.println("processing guild ranking.");
        return convertGuildRankingFromPrivate(getGuildRankingData(server), server);
    }

    @Scheduled(cron="0 0 * * * *")
    @CacheEvict(allEntries = true, value = "guild-ranking")
    public void clearCache() {
    }

    private GuildRankingWrapper getGuildRankingData(QuestlandServer server) {
        String baseUrl = regionWorkerMap.get(server);

        HttpHeaders headers = QueryUtils.getHttpHeaders(playerTokenMap.get(server));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("type", "global_guild");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        return restTemplate.exchange(baseUrl + "/rankings/getguildranking/",
                HttpMethod.POST,
                entity,
                GuildRankingWrapper.class).getBody();
    }

    private GuildRanking convertGuildRankingFromPrivate(GuildRankingWrapper guildRankingWrapper, QuestlandServer server) {
        int[][] ladder = guildRankingWrapper.getData().getRankingList().getLadder();

        List<RankedGuild> rankedGuilds = new ArrayList<>();
        for (int i = 0; i < ladder.length; i++) {
            RankedGuild rankedGuild = com.questland.handbook.publicmodel.RankedGuild.builder()
                    .rank(i + 1)
                    .guildId(ladder[i][0])
                    .guildScore(ladder[i][1])
                    .name(guildService.getGuildById(server, ladder[i][0]).getName())
                    .build();
            rankedGuilds.add(rankedGuild);
        }

        return GuildRanking.builder()
                .rankings(rankedGuilds)
                .build();
    }
}
