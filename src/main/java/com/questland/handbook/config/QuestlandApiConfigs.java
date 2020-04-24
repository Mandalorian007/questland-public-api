package com.questland.handbook.config;

import static com.questland.handbook.config.QuestlandServer.AMERICA;
import static com.questland.handbook.config.QuestlandServer.ASIA;
import static com.questland.handbook.config.QuestlandServer.EUROPE;
import static com.questland.handbook.config.QuestlandServer.GLOBAL;
import static com.questland.handbook.config.QuestlandServer.VETERANS;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestlandApiConfigs {

  @Value("${GLOBAL_PLAYER_TOKEN}")
  private String globalPlayerToken;
  @Value("${AMERICA_PLAYER_TOKEN}")
  private String americaPlayerToken;
  @Value("${EUROPE_PLAYER_TOKEN}")
  private String europePlayerToken;
  @Value("${ASIA_PLAYER_TOKEN}")
  private String asiaPlayerToken;
  @Value("${VETERANS_PLAYER_TOKEN}")
  private String veteransPlayerToken;

  @Bean
  public Map<QuestlandServer, String> regionWorkerMap() {
    return Map.of(
        GLOBAL, "http://gs-global-wrk-04.api-ql.com/",
        EUROPE, "http://gs-eu2-wrk-02.api-ql.com/",
        AMERICA, "http://gs-bhs-wrk-01.api-ql.com/",
        ASIA, "http://gs-as-wrk-01.api-ql.com/",
        VETERANS, "http://3.eu-api-questland.gamesture.com/"
    );
  }

  @Bean
  public Map<QuestlandServer, String> playerTokenMap() {
    return Map.of(
        GLOBAL, globalPlayerToken,
        EUROPE, europePlayerToken,
        AMERICA, americaPlayerToken,
        ASIA, asiaPlayerToken,
        VETERANS, veteransPlayerToken
    );
  }
}
