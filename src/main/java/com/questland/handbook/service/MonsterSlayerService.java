package com.questland.handbook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.BattleLocation;
import com.questland.handbook.publicmodel.MonsterSlayerScore;
import com.questland.handbook.publicmodel.Stage;
import com.questland.handbook.publicmodel.StageMob;
import com.questland.handbook.publicmodel.StageScore;
import com.questland.handbook.service.model.questgroups.QuestGroupObject;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
  private final String mobsEndpoint =
      "staticdata/key/en/android/%s/static_mobs_on_stages/";
  private final int SLAYER_TOKEN_QUEST_ID = 1709;
  private final int STEW_USE_QUEST_ID = 1019;

  private Map<Integer, String> mobIdToNameCache;

  public MonsterSlayerScore getStageScores() {
    MONSTER_SLAYER_QUEST_ID = getMonsterSlayerQuestId();
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
        .mapToInt(mob -> mobIdAndPoints.getOrDefault(mob.getId(), 0)).sum();
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
                object -> (Integer)((List<?>)object).get(0),
                object -> (Integer)((List<?>)object).get(2)));
    questIdToPointsMap.remove(SLAYER_TOKEN_QUEST_ID);
    questIdToPointsMap.remove(STEW_USE_QUEST_ID);
    return questIdToPointsMap;
  }

  private int getMonsterSlayerQuestId() {
    //TODO user the player login to get active rank-able quests and figure out the monster slayer quest id
    //Note: return -1 or something when monster slayer is not active
    return 1597;
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
