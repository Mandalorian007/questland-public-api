package com.questland.handbook.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.questland.handbook.ItemRepository;
import com.questland.handbook.loader.model.PrivateItem;
import com.questland.handbook.loader.model.PrivateLink;
import com.questland.handbook.loader.model.PrivateStats;
import com.questland.handbook.model.Item;
import com.questland.handbook.model.ItemSlot;
import com.questland.handbook.model.Quality;
import com.questland.handbook.model.Stat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemLoader implements ApplicationRunner {

  private final ItemRepository itemRepository;
  private final RestTemplate restTemplate = new RestTemplate();
  private final String latestTokenUrl =
      "http://gs-bhs-wrk-02.api-ql.com/client/checkstaticdata/?lang=en&graphics_quality=hd_android";
  private final String itemUrl =
      "http://gs-bhs-wrk-01.api-ql.com/staticdata/key/en/android/%s/item_templates/";

  @Override
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
        .map(this::covertItemFromPrivate)

        .collect(Collectors.toList());

    log.info("Loading " + items.size() + " items into database...");
    itemRepository.saveAll(items);
    log.info("Database load of " + itemRepository.count() + " items complete.");


  }

  private Item covertItemFromPrivate(PrivateItem privateItem) {
    return Item.builder()
        .id(privateItem.getLinkId())
        .name(privateItem.getName())
        .quality(convertQualityFromPrivate(privateItem.getQuality()))
        .itemSlot(covertItemSlotFromPrivate(privateItem.getItemType()))
        .potential(convertPotentialFromPrivate(privateItem.getStats()))
        .attack(convertAttackFromPrivate(privateItem.getStats()))
        .magic(convertMagicFromPrivate(privateItem.getStats()))
        .defense(convertDefenseFromPrivate(privateItem.getStats()))
        .health(convertHealthFromPrivate(privateItem.getStats()))
        .itemBonus(covertItemBonusFromPrivate(privateItem.getLinks()))
        .itemLinks(convertItemLinksFromPrivate(privateItem.getLinks()))
        .orbBonus(covertOrbBonusFromPrivate(privateItem.getLinks()))
        .orbLinks(convertOrbLinksFromPrivate(privateItem.getLinks()))
        .build();
  }

  private static ItemSlot covertItemSlotFromPrivate(String privateItemType) {
    switch (privateItemType) {
      case "head":
        return ItemSlot.HELM;
      case "chest":
        return ItemSlot.CHEST;
      case "gloves":
        return ItemSlot.GLOVES;
      case "feet":
        return ItemSlot.BOOTS;
      case "amulet":
        return ItemSlot.NECKLACE;
      case "ring":
        return ItemSlot.RING;
      case "talisman":
        return ItemSlot.TALISMAN;
      case "main_hand":
        return ItemSlot.MAIN_HAND;
      case "off_hand":
        return ItemSlot.OFF_HAND;
      default:
        throw new RuntimeException(
            "Unable to parse Item slot for " + privateItemType + " while loading gear/item data");
    }
  }

  private static Quality convertQualityFromPrivate(String privateQuality) {
    switch (privateQuality) {
      case "common":
        return Quality.COMMON;
      case "uncommon":
        return Quality.UNCOMMON;
      case "rare":
        return Quality.RARE;
      case "epic":
        return Quality.EPIC;
      case "legendary":
        return Quality.LEGENDARY;
      case "artifact1":
        return Quality.ARTIFACT1;
      case "artifact2":
        return Quality.ARTIFACT2;
      case "artifact3":
        return Quality.ARTIFACT3;
      case "artifact4":
        return Quality.ARTIFACT4;
      default:
        throw new RuntimeException(
            "Unable to parse quality for " + privateQuality + " while loading gear/item data");
    }
  }

  private static int convertPotentialFromPrivate(PrivateStats stats) {
    return stats.getAttack()[1] + stats.getMagic()[1] + stats.getDefense()[1] + stats
        .getHealth()[1];
  }

  private static int convertAttackFromPrivate(PrivateStats stats) {
    return stats.getAttack()[0];
  }

  private static int convertMagicFromPrivate(PrivateStats stats) {
    return stats.getMagic()[0];
  }

  private static int convertDefenseFromPrivate(PrivateStats stats) {
    return stats.getDefense()[0];
  }

  private static int convertHealthFromPrivate(PrivateStats stats) {
    return stats.getHealth()[0];
  }

  private static Stat covertItemBonusFromPrivate(List<PrivateLink> links) {
    Optional<PrivateLink> gearLink = extractGearLink(links);
    if (gearLink.isPresent()) {
      return convertStatFromPrivate(gearLink.get().getBonusStat());
    }
    return Stat.NONE;
  }

  private static Stat covertOrbBonusFromPrivate(List<PrivateLink> links) {
    Optional<PrivateLink> orbLink = extractOrbLink(links);
    if (orbLink.isPresent()) {
      return convertStatFromPrivate(orbLink.get().getBonusStat());
    }
    return Stat.NONE;
  }

  private List<Integer> convertItemLinksFromPrivate(List<PrivateLink> links) {
    Optional<PrivateLink> gearLink = extractGearLink(links);
    return linkExtractor(gearLink);
  }

  private List<Integer> convertOrbLinksFromPrivate(List<PrivateLink> links) {
    Optional<PrivateLink> orbLink = extractOrbLink(links);
    return linkExtractor(orbLink);
  }

  private static List<Integer> linkExtractor(Optional<PrivateLink> gearLink) {
    if (gearLink.isPresent()) {
      int[][] linkData = gearLink.get().getLinkData();
      List<Integer> linkIds = new ArrayList<>();
      for (int[] linkItem : linkData) {
        linkIds.add(linkItem[0]);
      }
      return linkIds;
    }
    return null;
  }

  private static Stat convertStatFromPrivate(String stat) {
    switch (stat) {
      case "attack":
        return Stat.ATTACK;
      case "magic":
        return Stat.MAGIC;
      case "damage":
        return Stat.ATTACK_MAGIC;
      case "defense":
      case "defence":
        return Stat.DEFENSE;
      case "health":
      case "hp":
        return Stat.HEALTH;
      default:
        return Stat.NONE;
    }
  }

  private static Optional<PrivateLink> extractGearLink(List<PrivateLink> privateLinks) {
    if (privateLinks == null || privateLinks.size() == 0) {
      return Optional.empty();
    }

    if (privateLinks.size() == 2) {
      // Gear link should always be the first entry
      return Optional.of(privateLinks.get(0));
    }

    return Optional.empty();
  }

  private static Optional<PrivateLink> extractOrbLink(List<PrivateLink> privateLinks) {
    if (privateLinks == null || privateLinks.size() == 0) {
      return Optional.empty();
    }

    if (privateLinks.size() == 2) {
      // Orb link should always be the second entry
      return Optional.of(privateLinks.get(1));
    }

    return Optional.empty();
  }
}
