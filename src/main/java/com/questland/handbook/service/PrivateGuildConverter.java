package com.questland.handbook.service;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.Guild;
import com.questland.handbook.publicmodel.GuildMember;
import com.questland.handbook.service.model.guild.PrivateGuildDetails;
import com.questland.handbook.service.model.guild.PrivateGuildMember;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PrivateGuildConverter {

  public Guild convertGuildFromPrivate(QuestlandServer server,
                                       PrivateGuildDetails privateGuildDetails) {

    List<GuildMember> guildMembers = privateGuildDetails.getMembers().stream()
        .map(this::convertGuildMemberFromPrivate)
        .collect(Collectors.toList());

    return Guild.builder()
        .guildId(privateGuildDetails.getId())
        .server(server)
        .name(privateGuildDetails.getName())
        .description(privateGuildDetails.getDesc())
        .level(privateGuildDetails.getLvl())
        .currentMemberCount(privateGuildDetails.getCnt())
        .maximumMemberCount(privateGuildDetails.getMcnt())
        .attackResearchLevel(extractResearchLevel(privateGuildDetails.getAcademy().getDamage()))
        .defenseResearchLevel(extractResearchLevel(privateGuildDetails.getAcademy().getDefense()))
        .healthResearchLevel(extractResearchLevel(privateGuildDetails.getAcademy().getHp()))
        .magicResearchLevel(extractResearchLevel(privateGuildDetails.getAcademy().getMagic()))
        .guildMembers(guildMembers)
        .build();
  }

  private GuildMember convertGuildMemberFromPrivate(PrivateGuildMember privateMember) {
    return GuildMember.builder()
        .id(privateMember.getId())
        .guildRank(privateMember.getGuildRank())
        .name(privateMember.getName())
        .level(privateMember.getLevel())
        .heroPower(privateMember.getPower())
        .build();
  }

  /*
  "damage": [
        9, - Level
        0, - Gold Contributed
        0, - Eternium Contributed
        0, - Blue Contributed
        0, - Red Contributed
        0,
        0
    ],
   */
  private int extractResearchLevel(List<Integer> researchDetails) {
    if (researchDetails == null || researchDetails.size() < 1) {
      return 0;
    }
    return researchDetails.get(0);
  }
}
