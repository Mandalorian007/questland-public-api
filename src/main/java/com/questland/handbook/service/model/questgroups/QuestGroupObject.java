package com.questland.handbook.service.model.questgroups;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.questland.handbook.service.MonsterSlayerService;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestGroupObject {

  private Object[] questGroup;

  /*
  This json object will be keyed by the heroId which we won't know in advance
  of parsing so we will be bypassing the name of that field and setting it directly
   */
  @JsonAnySetter
  public void setQuestGroupDetails(String name, Object[] questGroupDetails) {
    //Note the monster slayer quest id needs to be dynamic so this is a work around
    if (name.equals("" + MonsterSlayerService.MONSTER_SLAYER_QUEST_ID)) {
      questGroup = questGroupDetails;
    }
  }
}
