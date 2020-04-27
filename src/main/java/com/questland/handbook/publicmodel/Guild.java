package com.questland.handbook.publicmodel;

import com.questland.handbook.config.QuestlandServer;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guild {

  @Enumerated(EnumType.STRING)
  private QuestlandServer server;

  private int guildId;
  private String name;
  private String description;
  private int level;
  private int currentMemberCount;
  private int maximumMemberCount;
  private int attackResearchLevel;
  private int defenseResearchLevel;
  private int healthResearchLevel;
  private int magicResearchLevel;

  List<GuildMember> guildMembers;
}
