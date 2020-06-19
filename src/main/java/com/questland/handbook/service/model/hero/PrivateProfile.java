package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateProfile {
  private int id;
  @JsonProperty("guild_id")
  private int guildId;
  private int level;
  private String name;
  private String sex;
  private PrivateAttributes attributes;
  @JsonProperty("exists_days")
  private int heroAge;
  @JsonProperty("vip_status")
  private int vipLevel;
  private String lang;
  @JsonProperty("fame_lvl")
  private int fameLevel;
  private PrivateCollection collection1;
  private PrivateCollection collection2;

}
