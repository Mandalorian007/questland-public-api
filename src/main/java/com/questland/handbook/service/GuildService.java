package com.questland.handbook.service;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.Guild;
import com.questland.handbook.service.model.guild.PrivateGetGuild;
import com.questland.handbook.service.model.guild.PrivateGuildDetails;
import com.questland.handbook.service.model.guild.PrivateSearchGuild;
import com.questland.handbook.service.model.guild.PrivateSearchGuildDetails;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
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
}
