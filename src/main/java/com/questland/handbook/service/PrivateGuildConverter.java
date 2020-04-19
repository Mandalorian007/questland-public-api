package com.questland.handbook.service;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.model.Guild;
import com.questland.handbook.model.GuildMember;
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
}
