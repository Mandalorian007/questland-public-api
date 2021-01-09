package com.questland.handbook.service;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.HallOfFame;
import com.questland.handbook.publicmodel.RankedHero;
import com.questland.handbook.service.model.halloffame.HallOfFameLadderEntry;
import com.questland.handbook.service.model.halloffame.HallOfFameWrapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HallOfFameService {

    private RestTemplate restTemplate = new RestTemplate();

    @Resource()
    Map<QuestlandServer, String> playerTokenMap;

    @Resource()
    Map<QuestlandServer, String> regionWorkerMap;


    public HallOfFame getHallOfFame(QuestlandServer server) {
        return convertHallOfFameFromPrivate(getHallOfFameData(server));
    }

    private HallOfFameWrapper getHallOfFameData(QuestlandServer server) {
        String baseUrl = regionWorkerMap.get(server);

        HttpHeaders headers = QueryUtils.getHttpHeaders(playerTokenMap.get(server));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("type", "heropower");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        return restTemplate.exchange(baseUrl + "/rankings/getheroranking/",
                HttpMethod.POST,
                entity,
                HallOfFameWrapper.class).getBody();
    }

    private HallOfFame convertHallOfFameFromPrivate(HallOfFameWrapper hallOfFameWrapper) {
        HallOfFameLadderEntry[] ladder = hallOfFameWrapper.getData().getRankingList()[0].getLadder();
        List<RankedHero> rankedHeroes =
                Arrays.stream(ladder).map(entry ->
                        RankedHero.builder()
                                .rank(entry.getRank())
                                .heroId(entry.getHeroId())
                                .heroPower(entry.getHeroPower())
                                .name(entry.getName())
                                .build())
                        .collect(Collectors.toList());

        return HallOfFame.builder()
                .rankings(rankedHeroes)
                .build();
    }
}
