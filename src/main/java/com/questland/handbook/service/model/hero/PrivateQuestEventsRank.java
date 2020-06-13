package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateQuestEventsRank {

  private List<Integer> questEvents;

  @JsonCreator
  public PrivateQuestEventsRank(Map<String, Integer> propertyMap) {
    try {
      questEvents =
          propertyMap.keySet().stream().map(Integer::parseInt).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("failed to parse quest event data: " + propertyMap);
    }
    ;
  }
}
