package com.questland.handbook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.BattleLocation;
import com.questland.handbook.service.model.battlelocation.PrivateBattleLocation;
import com.questland.handbook.service.model.battlelocation.PrivateBattleLocationDetailsConverter;
import com.questland.handbook.service.model.battlelocation.PrivateMob;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@AllArgsConstructor
public class BattleLocationsService {

  @Resource()
  Map<QuestlandServer, String> regionWorkerMap;

  private final PrivateBattleLocationDetailsConverter privateBattleLocationDetailsConverter;
  private final RestTemplate restTemplate = new RestTemplate();
  private final String latestTokenEndpoint =
      "client/checkstaticdata/?lang=en&graphics_quality=hd_android";
  private final String battleLocationsEndpoint =
      "staticdata/key/en/android/%s/static_battle_locations_new/";
  private final String mobLocationsEndpoint =
      "staticdata/key/en/android/%s/static_mobs_on_stages/";

  private List<BattleLocation> battleLocationsCache;

  public List<BattleLocation> getBattleLocationDetails() {
    if (battleLocationsCache == null || battleLocationsCache.size() == 0) {
      battleLocationsCache = privateGetBattleLocations();
    }
    return battleLocationsCache;

  }

  private List<BattleLocation> privateGetBattleLocations() {
    try {
      String server = regionWorkerMap.get(QuestlandServer.GLOBAL);
      log.info(server + latestTokenEndpoint);
      String latestTokenResponse = restTemplate.getForObject(
          server + latestTokenEndpoint, String.class);

      JsonNode crcDetails = new ObjectMapper().readTree(latestTokenResponse)
          .path("data")
          .path("static_data")
          .path("crc_details");

      String battleLocationsToken = crcDetails.path("static_battle_locations_new").asText();
      log.info("Latest battle locations token is: " + battleLocationsToken);

      String mobLocationsToken = crcDetails.path("static_mobs_on_stages").asText();
      log.info("Latest mob locations token is: " + mobLocationsToken);

      String battleLocationEndpoint = String.format(battleLocationsEndpoint, battleLocationsToken);
      List<PrivateBattleLocation> privateBattleLocations = Arrays.asList(
          restTemplate
              .getForObject(server + battleLocationEndpoint, PrivateBattleLocation[].class));

      ParameterizedTypeReference<Map<Integer, PrivateMob>> responseType =
          new ParameterizedTypeReference<>() {
          };
      RequestEntity<Void> request = RequestEntity
          .get(new URI(server + String.format(mobLocationsEndpoint, mobLocationsToken)))
          .accept(MediaType.APPLICATION_JSON).build();

      Map<Integer, PrivateMob> privateMobs = restTemplate.exchange(request, responseType).getBody();

      return privateBattleLocationDetailsConverter
          .convertFromPrivate(privateBattleLocations, privateMobs);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
