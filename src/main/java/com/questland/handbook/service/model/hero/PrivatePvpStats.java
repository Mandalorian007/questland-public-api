package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivatePvpStats {

  private int wins;
  private int losses;
  private int score;
  private int place;
  @JsonProperty("def_heropower")
  private int defenseHeroPower;
  @JsonProperty("att_heropower")
  private int attackHeroPower;
}
