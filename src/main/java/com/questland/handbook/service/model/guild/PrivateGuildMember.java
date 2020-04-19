package com.questland.handbook.service.model.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateGuildMember {

  @JsonProperty("hero_id")
  private int id;
  private String name;
  private int level;
  private int power;
  @JsonProperty("guild_rank")
  private String guildRank;
}
