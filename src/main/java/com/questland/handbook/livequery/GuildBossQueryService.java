package com.questland.handbook.livequery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.questland.handbook.model.GuildBoss;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class GuildBossQueryService {

  @Value("${PLAYER_TOKEN}")
  private String playerToken;

  private final RestTemplate restTemplate = new RestTemplate();
  private final String playerLoginUrl = "http://gs-global-wrk-04.api-ql.com/client/init/";

  //TODO consider caching on this method
  public GuildBoss getCurrentGuildBoss() {
    if (playerToken == null) {
      log.error("Player token is missing!");
      return null;
    }

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("token", playerToken);

      String playerLoginInfo = restTemplate.exchange(
          playerLoginUrl,
          HttpMethod.GET,
          new HttpEntity<String>(headers),
          String.class).getBody();

      String guildBossName = new ObjectMapper().readTree(playerLoginInfo)
          .path("data")
          .path("ch_boss")
          .path("boss")
          .path("name").asText();

      return new GuildBoss(guildBossName);
    } catch (Exception e) {
      log.error("Failed to load guild boss data", e);
      return null;
    }

  }
}
