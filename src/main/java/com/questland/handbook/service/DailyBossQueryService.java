package com.questland.handbook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.model.DailyBoss;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class DailyBossQueryService {

  @Resource()
  Map<QuestlandServer, String> regionWorkerMap;

  @Resource()
  Map<QuestlandServer, String> playerTokenMap;

  private final RestTemplate restTemplate = new RestTemplate();

  public DailyBoss getCurrentDailyBoss(QuestlandServer server) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("token", playerTokenMap.get(server));

      String playerLoginInfo = restTemplate.exchange(
          regionWorkerMap.get(server) + "client/init/",
          HttpMethod.GET,
          new HttpEntity<String>(headers),
          String.class).getBody();

      String dailyBossName = new ObjectMapper().readTree(playerLoginInfo)
          .path("data")
          .path("ch_boss")
          .path("boss")
          .path("name").asText();

      return new DailyBoss(dailyBossName);
    } catch (Exception e) {
      log.error("Failed to load daily boss data", e);
      return null;
    }

  }
}
