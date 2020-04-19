package com.questland.handbook.controller;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.model.Guild;
import com.questland.handbook.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

//TODO remove when we want the feature live for everyone
@ApiIgnore
@RestController
@RequiredArgsConstructor
public class GuildEndpoints {

  private final GuildService guildService;

  @GetMapping("/guild/{name}")
  public Guild getGuild(@PathVariable("name") String name,
                        @RequestParam("server") String server) {
    return guildService.getGuild(QuestlandServer.valueOf(server), name)
        .orElseThrow(ResourceNotFoundException::new);
  }
}
