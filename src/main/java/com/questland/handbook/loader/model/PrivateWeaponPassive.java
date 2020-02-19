package com.questland.handbook.loader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateWeaponPassive {

  @JsonProperty("n")
  private String name;

  @JsonProperty("d")
  private String description;

  @JsonProperty("e")
  private String templateData;

  @JsonProperty("t")
  private int triggerChance;
}
