package com.questland.handbook.controller;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.Hero;
import com.questland.handbook.service.HeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
public class HeroEndpoints {

  private final HeroService heroService;

  @GetMapping("/hero/{guild}/{name}")
  public Hero getGuild(@PathVariable("guild") String guild,
                       @PathVariable("name") String name,
                       @RequestParam("server") String server) {
    return heroService.getHero(QuestlandServer.valueOf(server), guild, name)
        .orElseThrow(ResourceNotFoundException::new);
  }

  @GetMapping("/hero/{id}")
  public Hero getGuild(@PathVariable("id") int id,
                       @RequestParam("server") String server) {
    return heroService.getHero(QuestlandServer.valueOf(server), id)
        .orElseThrow(ResourceNotFoundException::new);
  }
}
