package com.questland.handbook.publicmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuildMember {

  private int id;
  private String name;
  private String guildRank;
  private int level;
  private int heroPower;
}
