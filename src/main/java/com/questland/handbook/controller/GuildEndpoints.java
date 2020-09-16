package com.questland.handbook.controller;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.Guild;
import com.questland.handbook.publicmodel.GuildPlan;
import com.questland.handbook.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GuildEndpoints {

  private final GuildService guildService;

  @GetMapping("/guild/{name}")
  public Guild getGuild(@PathVariable("name") String name,
                        @RequestParam("server") String server) {
    System.out.println("looking up guild: " + name + " on server: " + server);
    return guildService.getGuild(QuestlandServer.valueOf(server), name)
        .orElseThrow(ResourceNotFoundException::new);
  }

  @GetMapping("/guild/plan/{name}")
  public GuildPlan getGuildPlan(@PathVariable("name") String name,
                                @RequestParam("server") String server) {
    return guildService.getGuildPlanner(QuestlandServer.valueOf(server), name);
  }
}
