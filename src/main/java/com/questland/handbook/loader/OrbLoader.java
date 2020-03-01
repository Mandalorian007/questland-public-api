package com.questland.handbook.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.questland.handbook.OrbRepository;
import com.questland.handbook.loader.model.PrivateOrb;
import com.questland.handbook.model.Orb;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrbLoader implements ApplicationRunner {

  private final PrivateItemAndOrbConverter privateConverter;
  private final OrbRepository orbRepository;
  private final RestTemplate restTemplate = new RestTemplate();
  private final String latestTokenUrl =
      "http://gs-bhs-wrk-02.api-ql.com/client/checkstaticdata/?lang=en&graphics_quality=hd_android";
  private final String orbUrl =
      "http://gs-bhs-wrk-01.api-ql.com/staticdata/key/en/android/%s/item_templates/";

  @Override
  @Scheduled(cron = "0 0 0 ? * * *")
  public void run(ApplicationArguments args) throws Exception {
    String latestTokenResponse = restTemplate.getForObject(latestTokenUrl, String.class);

    String latestToken = new ObjectMapper().readTree(latestTokenResponse)
        .path("data")
        .path("static_data")
        .path("crc_details")
        .path("item_templates").asText();
    log.info("Latest item token is: " + latestToken);

    List<PrivateOrb> privateOrbs = Arrays.asList(
        restTemplate.getForObject(String.format(orbUrl, latestToken), PrivateOrb[].class));

    List<Orb> orbs = privateOrbs.stream()
        // Filter out any item that wouldn't be considered an orb
        .filter(item -> item.getItemType().equals("rune"))
        // Convert to our internal orb model
        .map(orb -> privateConverter.covertOrbFromPrivate(orb))
        .collect(Collectors.toList());

    log.info("dropping existing orb table");
    orbRepository.deleteAll();

    log.info("Loading " + orbs.size() + " orbs into database...");
    orbRepository.saveAll(orbs);
    log.info("Database load of " + orbRepository.count() + " orbs complete.");

  }

}
