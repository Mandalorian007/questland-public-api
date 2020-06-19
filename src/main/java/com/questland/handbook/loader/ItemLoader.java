package com.questland.handbook.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.questland.handbook.loader.model.PrivateItem;
import com.questland.handbook.loader.model.PrivateWeaponPassive;
import com.questland.handbook.publicmodel.Emblem;
import com.questland.handbook.publicmodel.Item;
import com.questland.handbook.publicmodel.Quality;
import com.questland.handbook.repository.ItemRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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
public class ItemLoader implements ApplicationRunner {

  private final PrivateItemAndOrbConverter privateConverter;
  private final ItemRepository itemRepository;
  private final RestTemplate restTemplate = new RestTemplate();
  private final String latestTokenUrl =
      "http://gs-bhs-wrk-02.api-ql.com/client/checkstaticdata/?lang=en&graphics_quality=hd_android";
  private final String itemUrl =
      "http://gs-bhs-wrk-01.api-ql.com/staticdata/key/en/android/%s/item_templates/";
  private final String setEmblemUrl =
      "http://gs-bhs-wrk-01.api-ql.com/staticdata/key/en/android/%s/wearable_sets/";
  private final String weaponPassivesUrl =
      "http://gs-bhs-wrk-01.api-ql.com/staticdata/key/en/android/%s/static_passive_skills/";

  private Set<String> manuallyWhitelistedNames = Set.of(
      "Ring of the Cursed Depths",
      "The Lost Helm",
      "Grips of the Depths",
      "Sea Hag’s Blessing",
      "Cursed Seafarer Armor",
      "Seafarer’s Spyglass",
      "Jolt of Lightning",
      "Thunderbolt Stompers",
      "Thunderclap",
      "Booming Blade",
      "Sphere of the Tempest",
      "Warfists of Thunder",
      "Heart of the Storm",
      "Healing Spark",
      "Knight of the Tempest Armor",
      "Helm of the Storm",
      "Bolt of Lightning Ring"
  );

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

    List<PrivateItem> privateItems = Arrays.asList(
        restTemplate.getForObject(String.format(itemUrl, latestToken), PrivateItem[].class));
    log.info("# of items discovered: " + privateItems.size());

    Map<Integer, Emblem> emblemMap = getEmblemMap();

    Map<Integer, PrivateWeaponPassive> weaponPassives = getWeaponPassives();
    log.info("Loaded " + weaponPassives.size() + " weapon passives");

    Set<String> validItemTypes = Set.of(
        "head",
        "chest",
        "gloves",
        "feet",
        "amulet",
        "ring",
        "talisman",
        "main_hand",
        "off_hand");

    List<Item> items = privateItems.stream()
        // Filter out any item that wouldn't be considered gear
        .filter(item -> validItemTypes.contains(item.getItemType()))
        // Convert to our internal gear model
        .map(item -> privateConverter.covertItemFromPrivate(item, emblemMap, weaponPassives))
        .collect(Collectors.toList());

    items = applyWhitelist(items);

    log.info("dropping existing item table");
    itemRepository.deleteAll();

    log.info("Loading " + items.size() + " items into database...");
    itemRepository.saveAll(items);
    log.info("Database load of " + itemRepository.count() + " items complete.");
  }


  private Map<Integer, Emblem> getEmblemMap() {
    String latestTokenResponse = restTemplate.getForObject(latestTokenUrl, String.class);
    Map<Integer, Emblem> emblemMap = new HashMap<>();
    try {
      String emblemToken = new ObjectMapper().readTree(latestTokenResponse)
          .path("data")
          .path("static_data")
          .path("crc_details")
          .path("wearable_sets").asText();
      log.info("Latest set/emblem token is: " + emblemToken);

      String emblemUrl = String.format(setEmblemUrl, emblemToken);
      log.info(emblemUrl);
      String emblemDataRaw =
          restTemplate.getForObject(emblemUrl, String.class);

      JsonNode emblemArray = new ObjectMapper().readTree(emblemDataRaw);
      for (JsonNode emblemEntry : emblemArray) {
        Iterable<JsonNode> iterable = emblemEntry::elements;
        List<JsonNode> emblemEntryList =
            StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
        emblemMap.put(
            emblemEntryList.get(0).asInt(),
            PrivateItemAndOrbConverter.getEmblemFromPrivate(emblemEntryList.get(1).asText()));
      }
      return emblemMap;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private Map<Integer, PrivateWeaponPassive> getWeaponPassives() {
    try {
      String latestTokenResponse = restTemplate.getForObject(latestTokenUrl, String.class);

      String passiveToken = new ObjectMapper().readTree(latestTokenResponse)
          .path("data")
          .path("static_data")
          .path("crc_details")
          .path("static_passive_skills").asText();
      log.info("Latest weapon passives token is: " + passiveToken);

      String passiveWeaponsUrl = String.format(weaponPassivesUrl, passiveToken);
      String passiveItemsRaw =
          restTemplate.getForObject(passiveWeaponsUrl, String.class);

      TypeReference<HashMap<Integer, PrivateWeaponPassive>> typeRef = new TypeReference<>() {
      };
      return new ObjectMapper().readValue(passiveItemsRaw, typeRef);

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private List<Item> applyWhitelist(List<Item> items) {
    Set<Quality> artifactQualities = Set.of(
        Quality.ARTIFACT1,
        Quality.ARTIFACT2,
        Quality.ARTIFACT3,
        Quality.ARTIFACT4
    );
    Set<String> whiteListedNames = items.stream()
        .filter(item -> !artifactQualities.contains(item.getQuality()))
        .filter(item -> item.getTotalPotential() < 210)
        .map(Item::getName)
        .collect(Collectors.toSet());
    whiteListedNames.addAll(manuallyWhitelistedNames);

    return items.stream()
        .map(item -> {
          if(!whiteListedNames.contains(item.getName())) {
            item.setHidden(true);
            if (item.getQuality() == Quality.LEGENDARY) {
              log.info("Hidden Item: " + item.toString());
            }
          }
          return item;
        })
        .collect(Collectors.toList());
  }

}
