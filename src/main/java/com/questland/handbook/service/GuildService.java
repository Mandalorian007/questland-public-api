package com.questland.handbook.service;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.controller.ResourceNotFoundException;
import com.questland.handbook.publicmodel.Guild;
import com.questland.handbook.publicmodel.GuildPlan;
import com.questland.handbook.publicmodel.Hero;
import com.questland.handbook.publicmodel.HeroPlan;
import com.questland.handbook.service.model.guild.PrivateGetGuild;
import com.questland.handbook.service.model.guild.PrivateGuildDetails;
import com.questland.handbook.service.model.guild.PrivateSearchGuild;
import com.questland.handbook.service.model.guild.PrivateSearchGuildDetails;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GuildService {

  private final PrivateGuildConverter privateGuildConverter;

  private HeroService heroService;
  private RestTemplate restTemplate = new RestTemplate();

  @Resource()
  Map<QuestlandServer, String> playerTokenMap;

  @Resource()
  Map<QuestlandServer, String> regionWorkerMap;

  public Optional<Guild> getGuild(QuestlandServer server, String guildName) {

    return searchForGuild(server, guildName).stream()
        .filter(details -> details.getName().equalsIgnoreCase(guildName))
        .map(details -> getGuildById(server, details.getId()))
        .map(details -> privateGuildConverter.convertGuildFromPrivate(server, details))
        .findFirst();
  }

  public PrivateGuildDetails getGuildById(QuestlandServer server, int guildId) {
    String baseUrl = regionWorkerMap.get(server);
    HttpHeaders headers = QueryUtils.getHttpHeaders(playerTokenMap.get(server));

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("guild_id", "" + guildId);

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

    ResponseEntity<PrivateGetGuild> response =
        restTemplate.exchange(baseUrl + "/guild/getguild/",
            HttpMethod.POST,
            entity,
            PrivateGetGuild.class);
    return response.getBody().getData().getGuildDetailList().get(0);

  }

  public GuildPlan getGuildPlanner(QuestlandServer server, String guildName) {
    Optional<Guild> guildOptional = getGuild(server, guildName);
    Guild guild = guildOptional.orElseThrow(ResourceNotFoundException::new);
    List<HeroPlan> heroPlans = guild.getGuildMembers().stream()
        .map(member -> heroService.getHero(server, member.getId()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(this::getPlanFrom)
        .sorted(Comparator.comparingDouble(HeroPlan::getBeHeroPower).reversed())
        .collect(Collectors.toList());

    return GuildPlan.builder()
        .guildId(guild.getGuildId())
        .name(guild.getName())
        .heroPlans(heroPlans)
        .build();
  }

  private List<PrivateSearchGuildDetails> searchForGuild(QuestlandServer server, String name) {
    String baseUrl = regionWorkerMap.get(server);
    HttpHeaders headers = QueryUtils.getHttpHeaders(playerTokenMap.get(server));
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("name", name.replace("'", "\\'"));

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

    ResponseEntity<PrivateSearchGuild> result =
        restTemplate.exchange(baseUrl + "/guild/searchguild/",
            HttpMethod.POST,
            entity,
            PrivateSearchGuild.class);

    if (result.getBody() != null && result.getBody().getData() != null) {
      return result.getBody().getData().getGuildDetailList();
    }
    return Collections.emptyList();
  }

  private HeroPlan getPlanFrom(Hero hero) {
    double multi = hero.getBattleEventMulti();
    double beHealth = hero.getHealth() * multi;
    double beAttack = hero.getAttack() * multi;
    double beDefense = hero.getDefense() * multi;
    double beMagic = hero.getMagic() * multi;
    return HeroPlan.builder()
        .id(hero.getId())
        .name(hero.getName())
        .heroPower(hero.getHeroPower())
        .health(hero.getHealth())
        .attack(hero.getAttack())
        .defense(hero.getDefense())
        .magic(hero.getMagic())
        .battleEventMulti(multi)
        .beHeroPower(beHealth + beAttack + beDefense + beMagic)
        .beHealth(beHealth)
        .beAttack(beAttack)
        .beDefense(beDefense)
        .beMagic(beMagic)
        .row1Bonus(hero.getRow1Bonus())
        .row2Bonus(hero.getRow2Bonus())
        .row3Bonus(hero.getRow3Bonus())
        .row4Bonus(hero.getRow4Bonus())
        .build();
  }

  @Autowired
  public void setHeroService(HeroService heroService) {
    this.heroService = heroService;
  }
}
