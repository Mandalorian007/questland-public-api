package com.questland.handbook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.Guild;
import com.questland.handbook.publicmodel.GuildMember;
import com.questland.handbook.publicmodel.Hero;
import com.questland.handbook.service.model.hero.PrivateGetProfile;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeroService {

  private final GuildService guildService;
  private final PrivateHeroConverter privateHeroConverter;

  private RestTemplate restTemplate = new RestTemplate();

  @Resource()
  Map<QuestlandServer, String> playerTokenMap;

  @Resource()
  Map<QuestlandServer, String> regionWorkerMap;

  public Optional<Hero> getHero(QuestlandServer server, String guildName, String heroName) {
    Optional<Guild> guild = guildService.getGuild(server, guildName);
    if (guild.isEmpty()) {
      return Optional.empty();
    }
    Optional<GuildMember> hero = guild.get().getGuildMembers().stream()
        .filter(member -> member.getName().equalsIgnoreCase(heroName))
        .findFirst();

    if (hero.isEmpty()) {
      return Optional.empty();
    }
    int heroId = hero.get().getId();

    return getHero(server, heroId);
  }

  public Optional<Hero> getHero(QuestlandServer server, int heroId) {
    PrivateGetProfile privateHero = heroLookup(server, heroId);
    Hero hero = privateHeroConverter.convertHeroFromPrivate(server, privateHero.getData());
    if (hero == null) {
      return Optional.empty();
    }
    return Optional.of(hero);
  }

  private PrivateGetProfile heroLookup(QuestlandServer server, int heroId) {
    String baseUrl = regionWorkerMap.get(server);
    HttpHeaders headers = QueryUtils.getHttpHeaders(playerTokenMap.get(server));

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("hero_id", "" + heroId);

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

    ResponseEntity<String> response =
        restTemplate.exchange(baseUrl + "/user/getprofile/",
            HttpMethod.POST,
            entity,
            String.class);
    try {
      return getMapper().readValue(response.getBody(), PrivateGetProfile.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

  }

  private ObjectMapper getMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
    return objectMapper;
  }

}
