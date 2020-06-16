package com.questland.handbook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.BattleLocation;
import com.questland.handbook.publicmodel.MonsterSlayerScore;
import com.questland.handbook.publicmodel.Stage;
import com.questland.handbook.publicmodel.StageMob;
import com.questland.handbook.publicmodel.StageScore;
import com.questland.handbook.service.model.questgroups.QuestGroupObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.Cacheable;

@Slf4j
@Service
@AllArgsConstructor
public class MonsterSlayerService {

  public static Integer MONSTER_SLAYER_QUEST_ID = 1597;

  @Resource()
  private Map<QuestlandServer, String> playerTokenMap;
  @Resource()
  private Map<QuestlandServer, String> regionWorkerMap;

  private final BattleLocationsService battleLocationsService;

  private final RestTemplate restTemplate = new RestTemplate();
  private final String latestTokenEndpoint =
      "client/checkstaticdata/?lang=en&graphics_quality=hd_android";
  private final String questGroupEndpoint =
      "staticdata/key/en/android/%s/static_quest_groups/";
  private final String questListEndpoint =
      "staticdata/key/en/android/%s/static_quests_list_new/";
  private final int SLAYER_TOKEN_QUEST_ID = 1709;
  private final int STEW_USE_QUEST_ID = 1019;

  private Map<Integer, String> mobIdToNameCache;

  public MonsterSlayerScore getStageScores() {
    Optional<Integer> monsterSlayerQuestId = getMonsterSlayerQuestId();
    if (monsterSlayerQuestId.isEmpty()) {
      return new MonsterSlayerScore();
    }
    MONSTER_SLAYER_QUEST_ID = monsterSlayerQuestId.get();
    List<BattleLocation> battleLocationDetails = battleLocationsService.getBattleLocationDetails();
    if (mobIdToNameCache == null) {
      mobIdToNameCache = generateMobIdCache(battleLocationDetails);
    }
    Map<Integer, Integer> mobIdAndPoints = getMobIdAndPoints();

    return scoreStages(battleLocationDetails, mobIdAndPoints);
  }

  private Map<Integer, String> generateMobIdCache(List<BattleLocation> battleLocationDetails) {
    return battleLocationDetails.stream()
        .flatMap(battleLocation -> battleLocation.getStages().stream())
        .flatMap(stage -> stage.getStageMobs().stream())
        .collect(Collectors.toMap(StageMob::getId, StageMob::getName));
  }

  private MonsterSlayerScore scoreStages(List<BattleLocation> battleLocationDetails,
                                         Map<Integer, Integer> mobIdAndPoints) {
    List<StageScore> stageScores = battleLocationDetails.stream()
        .flatMap(battleLocation -> battleLocation.getStages().stream()
            .map(stage -> calculateStageScore(mobIdAndPoints, battleLocation, stage))
            .collect(Collectors.toList()).stream())
        .sorted(Comparator.comparingInt(StageScore::getStageScore).reversed())
        .collect(Collectors.toList());

    return MonsterSlayerScore.builder()
        .stageScores(stageScores)
        .build();
  }

  private StageScore calculateStageScore(Map<Integer, Integer> mobIdAndPoints,
                                         BattleLocation battleLocation, Stage stage) {
    int score = stage.getStageMobs().stream()
        .mapToInt(mob -> mobIdAndPoints.getOrDefault(mob.getId(), 0) * mob.getCount()).sum();
    return StageScore.builder()
        .locationNumber(battleLocation.getNumber())
        .locationName(battleLocation.getName())
        .stageNumber(stage.getStageNumber())
        .stageName(stage.getName())
        .stageScore(score)
        .build();
  }

  private Map<Integer, Integer> getMobIdAndPoints() {
    String questListToken = getTokenFor("static_quests_list_new");

    JsonNode questObject;
    try {
      String questListUrl = String.format(questListEndpoint, questListToken);
      String server = regionWorkerMap.get(QuestlandServer.GLOBAL);
      String questList = restTemplate.getForObject(
          server + questListUrl, String.class);
      questObject = new ObjectMapper().readTree(questList);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return getMonsterSlayerQuestsAndPoints().entrySet().stream()
        .collect(Collectors.toMap(
            entry -> extractMobIdFromQuest(questObject, entry.getKey()),
            Map.Entry::getValue));
  }

  private int extractMobIdFromQuest(JsonNode questObject, int questId) {
    int count = 0;
    for (JsonNode node : questObject.path("" + questId)) {
      if (count == 11) {
        return node.asInt();
      }
      count++;
    }
    return 0;
  }

  private Map<Integer, Integer> getMonsterSlayerQuestsAndPoints() {
    String questGroupsToken = getTokenFor("static_quest_groups");
    String server = regionWorkerMap.get(QuestlandServer.GLOBAL);

    try {
      String battleLocationEndpoint = String.format(questGroupEndpoint, questGroupsToken);
      QuestGroupObject questGroupObject = restTemplate
          .getForObject(server + battleLocationEndpoint, QuestGroupObject.class);

      return convertQuestGroupArray((List<?>) questGroupObject.getQuestGroup()[9]);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Map<Integer, Integer> convertQuestGroupArray(List<?> questList) {
    Map<Integer, Integer> questIdToPointsMap = questList.stream()
        .collect(
            Collectors.toMap(
                object -> (Integer) ((List<?>) object).get(0),
                object -> (Integer) ((List<?>) object).get(2)));
    questIdToPointsMap.remove(SLAYER_TOKEN_QUEST_ID);
    questIdToPointsMap.remove(STEW_USE_QUEST_ID);
    System.out.println(questIdToPointsMap);
    return questIdToPointsMap;
  }

  private Optional<Integer> getMonsterSlayerQuestId() {
    List<Integer> monsterSlayerQuestEvents = determineMonsterSlayerQuestEvents();

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("token", playerTokenMap.get(QuestlandServer.GLOBAL));

      String playerLoginInfo = restTemplate.exchange(
          regionWorkerMap.get(QuestlandServer.GLOBAL) + "client/init/",
          HttpMethod.GET,
          new HttpEntity<String>(headers),
          String.class).getBody();

      JsonNode activeQuests = new ObjectMapper().readTree(playerLoginInfo)
          .path("data")
          .path("hero_quest_events_rank");

      List<Integer> activeQuestIds = new ArrayList<>();
      activeQuests.fieldNames().forEachRemaining(idAsString ->
          activeQuestIds.add(Integer.parseInt(idAsString)));

      return activeQuestIds.stream()
          .filter(monsterSlayerQuestEvents::contains)
          .findFirst();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Cacheable("monster-slayer-quests")
  private List<Integer> determineMonsterSlayerQuestEvents() {
    List<Integer> monsterSlayerQuestEventIds = new ArrayList<>();

    String questGroupsToken = getTokenFor("static_quest_groups");
    String server = regionWorkerMap.get(QuestlandServer.GLOBAL);

    try {
      String battleLocationEndpoint = String.format(questGroupEndpoint, questGroupsToken);
      String result = restTemplate
          .getForObject(server + battleLocationEndpoint, String.class);
      ObjectNode objectNode = (ObjectNode) new ObjectMapper().readTree(result);

      Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> jsonArray = fields.next();
        int count = 0;
        for (JsonNode node : jsonArray.getValue()) {
          if (count == 2) {
            for (JsonNode nestedNode : node) {
              if (nestedNode.asInt() == 44901) {
                monsterSlayerQuestEventIds.add(Integer.parseInt(jsonArray.getKey()));
                break;
              }
            }
          }
          count++;
        }
      }
      return monsterSlayerQuestEventIds;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String getTokenFor(String tokenKey) {
    try {
      String server = regionWorkerMap.get(QuestlandServer.GLOBAL);
      String latestTokenResponse = restTemplate.getForObject(
          server + latestTokenEndpoint, String.class);

      JsonNode crcDetails = new ObjectMapper().readTree(latestTokenResponse)
          .path("data")
          .path("static_data")
          .path("crc_details");

      String token = crcDetails.path(tokenKey).asText();
      log.info("Latest " + tokenKey + " token is: " + token);
      return token;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
