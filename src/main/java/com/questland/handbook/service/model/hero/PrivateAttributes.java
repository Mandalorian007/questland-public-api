package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateAttributes {
  private int dmg;
  private int def;
  private int hp;
  private int magic;
  private int heropower;
  private double critval;
  private int critchance;
  private int dodge;
}
