package com.questland.handbook.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.model.Guild;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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

  @Value("${PLAYER_TOKEN}")
  private String playerToken;

  @Resource()
  Map<QuestlandServer, String> regionWorkerMap;

  public Optional<Guild> getGuild(QuestlandServer server, String guildName) {

    return searchForGuild(server, guildName).stream()
        .filter(details -> details.getName().equalsIgnoreCase(guildName))
        .map(details -> guildDetails(server, details))
        .map(details -> privateGuildConverter.convertGuildFromPrivate(server, details))
        .findFirst();
  }

  private PrivateGuildDetails guildDetails(QuestlandServer server,
                                           PrivateSearchGuildDetails details) {
    String baseUrl = regionWorkerMap.get(server);
    HttpHeaders headers = getHttpHeaders();

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("guild_id", "" + details.getId());

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
    HttpHeaders headers = getHttpHeaders();
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("name", name);

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

  private HttpHeaders getHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.add("token", playerToken);
    headers.add("Accept", APPLICATION_JSON.getType());
    headers.add("Content-Type", "application/x-www-form-urlencoded");
    return headers;
  }
}
