package com.questland.handbook.config;

import static com.questland.handbook.config.QuestlandRegion.*;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestlandApiConfigs {

  @Bean
  public Map<QuestlandRegion, String> regionWorkerMap() {
    //TODO resolve workers for other servers
    return Map.of(
        GLOBAL, "http://gs-global-wrk-04.api-ql.com/",
        EUROPE, "http://gs-bhs-wrk-01.api-ql.com/",
        AMERICA, "UNKNOWN",
        ASIA, "UNKNOWN",
        VETERANS, "UNKNOWN"
    );
  }
}
