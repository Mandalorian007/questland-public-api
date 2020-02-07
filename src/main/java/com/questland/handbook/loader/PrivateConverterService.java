package com.questland.handbook.loader;

import com.questland.handbook.loader.model.PrivateItem;
import com.questland.handbook.loader.model.PrivateLink;
import com.questland.handbook.loader.model.PrivateStats;
import com.questland.handbook.model.Emblem;
import com.questland.handbook.model.Item;
import com.questland.handbook.model.ItemSlot;
import com.questland.handbook.model.Quality;
import com.questland.handbook.model.Stat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PrivateConverterService {

  public Item covertItemFromPrivate(PrivateItem privateItem, Map<Integer, Emblem> emblemMap) {
    return Item.builder()
        .id(privateItem.getLinkId())
        .name(privateItem.getName())
        .quality(convertQualityFromPrivate(privateItem.getQuality()))
        .itemSlot(covertItemSlotFromPrivate(privateItem.getItemType()))
        .emblem(emblemMap.getOrDefault(privateItem.getSet(), Emblem.UNKNOWN))
        .totalPotential(convertTotalPotentialFromPrivate(privateItem.getStats()))
        .attack(convertAttackFromPrivate(privateItem.getStats()))
        .magic(convertMagicFromPrivate(privateItem.getStats()))
        .defense(convertDefenseFromPrivate(privateItem.getStats()))
        .health(convertHealthFromPrivate(privateItem.getStats()))
        .attackPotential(convertAttackPotentialFromPrivate(privateItem.getStats()))
        .magicPotential(convertMagicPotentialFromPrivate(privateItem.getStats()))
        .defensePotential(convertDefensePotentialFromPrivate(privateItem.getStats()))
        .healthPotential(convertHealthPotentialFromPrivate(privateItem.getStats()))
        .itemBonus(covertItemBonusFromPrivate(privateItem.getLinks()))
        .itemLinks(convertItemLinksFromPrivate(privateItem.getLinks()))
        .orbBonus(covertOrbBonusFromPrivate(privateItem.getLinks()))
        .orbLinks(convertOrbLinksFromPrivate(privateItem.getLinks()))
        .build();
  }

  public static ItemSlot covertItemSlotFromPrivate(String privateItemType) {
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

  public static Emblem getEmblemFromPrivate(String emblem) {
    switch (emblem) {
      case "Sacred":
        return Emblem.SACRED;
      case "Necro":
        return Emblem.NECRO;
      case "Beast":
        return Emblem.BEAST;
      case "Nature":
        return Emblem.NATURE;
      case "Dragon":
        return Emblem.DRAGON;
      case "Shadow":
        return Emblem.SHADOW;
      case "Myth":
        return Emblem.MYTH;
      case "Ice":
        return Emblem.ICE;
      case "Venom":
        return Emblem.VENOM;
      case "Death":
        return Emblem.DEATH;
      case "Lava":
        return Emblem.LAVA;
      case "Hex":
        return Emblem.HEX;
      case "Noble":
        return Emblem.NOBLE;
      case "Thunder":
        return Emblem.THUNDER;
      case "Abyss":
        return Emblem.ABYSS;
      case "Wind":
        return Emblem.WIND;
      default:
        log.warn(
            "Discovered an unrecognized emblem: `" + emblem + "` this should be investigated.");
        return Emblem.UNKNOWN;
    }
  }

  public static Quality convertQualityFromPrivate(String privateQuality) {
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

  public static Stat convertStatFromPrivate(String stat) {
    switch (stat) {
      case "attack":
      case "damage":
        return Stat.ATTACK;
      case "magic":
        return Stat.MAGIC;
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

  private static int convertTotalPotentialFromPrivate(PrivateStats stats) {
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

  private static int convertAttackPotentialFromPrivate(PrivateStats stats) {
    return stats.getAttack()[1];
  }

  private static int convertMagicPotentialFromPrivate(PrivateStats stats) {
    return stats.getMagic()[1];
  }

  private static int convertDefensePotentialFromPrivate(PrivateStats stats) {
    return stats.getDefense()[1];
  }

  private static int convertHealthPotentialFromPrivate(PrivateStats stats) {
    return stats.getHealth()[1];
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

  private static Optional<PrivateLink> extractGearLink(List<PrivateLink> privateLinks) {
    if (privateLinks == null || privateLinks.size() == 0) {
      return Optional.empty();
    }

    if (privateLinks.size() == 2) {
      // Gear link should always be 30
      if (privateLinks.get(0).getBonusAmount() > 20)
        return Optional.of(privateLinks.get(0));
      else
        return Optional.of(privateLinks.get(1));
    }

    return Optional.empty();
  }

  private static Optional<PrivateLink> extractOrbLink(List<PrivateLink> privateLinks) {
    if (privateLinks == null || privateLinks.size() == 0) {
      return Optional.empty();
    }

    if (privateLinks.size() == 2) {
      // Orb link should always be 15
      if (privateLinks.get(1).getBonusAmount() < 20)
        return Optional.of(privateLinks.get(1));
      else
        return Optional.of(privateLinks.get(0));
    }

    return Optional.empty();
  }
}
