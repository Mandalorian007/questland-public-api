package com.questland.handbook.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.questland.handbook.flatbuffers.EquipSlot;
import com.questland.handbook.flatbuffers.ItemQuality;
import com.questland.handbook.flatbuffers.ItemTemplate;
import com.questland.handbook.flatbuffers.ItemTemplates;
import com.questland.handbook.repository.OrbRepository;
import com.questland.handbook.publicmodel.Orb;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
      "http://gs-bhs-wrk-01.api-ql.com/staticdata/fb/key/en/android/%s/fb_item_templates/";

  @Override
  @Scheduled(cron = "0 * * * *")
  public void run(ApplicationArguments args) throws Exception {
    String latestTokenResponse = restTemplate.getForObject(latestTokenUrl, String.class);

    String latestToken = new ObjectMapper().readTree(latestTokenResponse)
        .path("data")
        .path("static_data")
        .path("crc_details")
        .path("fb_item_templates").asText();
    log.info("Latest orb token is: " + latestToken);

    byte[] itemBytes = restTemplate.getForObject(String.format(orbUrl, latestToken), byte[].class);
    ItemTemplates itemTemplates = ItemTemplates.getRootAsItemTemplates(ByteBuffer.wrap(itemBytes));
    List<ItemTemplate> privateOrbs = new ArrayList<>();
    for(int i = 0; i < itemTemplates.templatesLength(); i++) {
      privateOrbs.add(itemTemplates.templates(i));
    }

    Set<Byte> validItemTypes = Set.of(
            ItemQuality.Uncommon,
            ItemQuality.Rare,
            ItemQuality.Epic,
            ItemQuality.Legendary,
            ItemQuality.LegendaryPlus
    );

    List<Orb> orbs = privateOrbs.stream()
        // Filter out any item that wouldn't be considered an orb
        .filter(orb -> orb.s() == EquipSlot.Orb)
        .filter(orb -> validItemTypes.contains(orb.q()))
        // Convert to our internal orb model
        .map(privateConverter::covertOrbFromPrivate)
        .collect(Collectors.toList());

    log.info("dropping existing orb table");
    orbRepository.deleteAll();

    log.info("Loading " + orbs.size() + " orbs into database...");
    orbRepository.saveAll(orbs);
    log.info("Database load of " + orbRepository.count() + " orbs complete.");

  }

}
