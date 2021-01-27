package com.questland.handbook.loader;

import com.questland.handbook.config.QuestlandApiConfigs;
import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.flatbuffers.*;
import com.questland.handbook.loader.model.PrivateWeaponPassive;
import com.questland.handbook.publicmodel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateItemAndOrbConverter {
    private final String GRAPHICS_SERVER_PATH = "/packs/";
    private final QuestlandApiConfigs questlandApiConfigs;
    private String graphicsServerUrl;

    public Item convertItemFromPrivate(ItemTemplate privateItem,
                                       Map<Long, Emblem> emblemMap,
                                       Map<Integer, PrivateWeaponPassive> weaponPassives,
                                       Map<Integer, Integer> rawToFinalGraphicsIdMap) {

        if (graphicsServerUrl == null) {
            String qlServer = questlandApiConfigs.regionWorkerMap().get(QuestlandServer.GLOBAL);
            graphicsServerUrl = LoaderUtility.getGraphicsServerUrl(qlServer);
        }
        Item.ItemBuilder builder = Item.builder();

        //weapon passives
        // Note: This needed to be moved above the rest of the builder... somehow the bite array was being consumed early
        int numOfPassives = privateItem.psklsLength();
        if (numOfPassives >= 1) {
            int passiveId = privateItem.pskls(0);
            builder.passive1Name(convertToPassiveNameFromPrivate(passiveId, weaponPassives));
            builder.passive1Description(convertToPassiveDescriptionFromPrivate(passiveId, weaponPassives));
        }
        if (numOfPassives >= 2) {
            int passiveId = privateItem.pskls(1);
            builder.passive2Name(convertToPassiveNameFromPrivate(passiveId, weaponPassives));
            builder.passive2Description(convertToPassiveDescriptionFromPrivate(passiveId, weaponPassives));
        }

        int totalPotential = privateItem.statsDmgInc() + privateItem.statsMagicInc()
                + privateItem.statsDefInc() + privateItem.statsHpInc();
        builder
                .id(privateItem.t())
                .hidden(false)// is shown in index: maybeItem.get().si()
                .name(privateItem.n())
                .quality(convertQualityFromPrivate(privateItem.q()))
                .itemSlot(covertItemSlotFromPrivate(privateItem.s()))
                .extractCost(privateItem.extractCost())
                .emblem(emblemMap.getOrDefault(privateItem.set(), Emblem.NONE))
                .totalPotential(totalPotential)
                .attack(privateItem.statsDmg())
                .magic(privateItem.statsMagic())
                .defense(privateItem.statsDef())
                .health(privateItem.statsHp())
                .attackPotential(privateItem.statsDmgInc())
                .magicPotential(privateItem.statsMagicInc())
                .defensePotential(privateItem.statsDefInc())
                .healthPotential(privateItem.statsHpInc())
                .reforgePointsPerLevel(totalPotential / 2)
                .iconGraphicsUrl(graphicsServerUrl + GRAPHICS_SERVER_PATH + rawToFinalGraphicsIdMap.getOrDefault(privateItem.iSd(), -1) + "/")
                .fullGraphicsUrl(graphicsServerUrl + GRAPHICS_SERVER_PATH + rawToFinalGraphicsIdMap.getOrDefault(privateItem.prvw(), -1) + "/");

        //Item links exist
        if (privateItem.linksLength() >= 1) {
            Link itemLinks = privateItem.links(0);
            builder.itemBonus(covertItemBonusFromPrivate(itemLinks.e()));

            int numOfLinks = itemLinks.iLength();
            if (numOfLinks >= 1) {
                builder.itemLink1(itemLinks.i(0));
            }
            if (numOfLinks >= 2) {
                builder.itemLink2(itemLinks.i(1));
            }
            if (numOfLinks >= 3) {
                builder.itemLink3(itemLinks.i(2));
            }
        } else {
            builder.itemBonus(Stat.NONE);
        }

        //Orb links exist
        if (privateItem.linksLength() >= 2) {
            Link orbLinks = privateItem.links(1);
            builder.orbBonus(covertItemBonusFromPrivate(orbLinks.e()));

            int numOfLinks = orbLinks.iLength();
            if (numOfLinks >= 1) {
                builder.orbLink1(orbLinks.i(0));
            }
            if (numOfLinks >= 2) {
                builder.orbLink2(orbLinks.i(1));
            }
        } else {
            builder.orbBonus(Stat.NONE);
        }

        return builder.build();
    }

    public Orb convertOrbFromPrivate(ItemTemplate privateOrb, Map<Integer, Integer> rawToFinalGraphicsIdMap) {
        int defense = privateOrb.statsDef();
        int health = privateOrb.statsHp();
        Stat stat;

        if (defense > 0) {
            stat = Stat.DEFENSE;
        } else if (health > 0) {
            stat = Stat.HEALTH;
        } else {
            stat = Stat.ATTACK_MAGIC;
        }

        return Orb.builder()
                .id(privateOrb.t())
                .name(privateOrb.n())
                .quality(convertQualityFromPrivate(privateOrb.q()))
                .attack(privateOrb.statsDmg())
                // magic stat is always identical to attack
                .magic(privateOrb.statsDmg())
                .defense(defense)
                .health(health)
                .attackPotential(privateOrb.statsDmgInc())
                // magic potential is always identical to attack
                .magicPotential(privateOrb.statsDmgInc())
                .defensePotential(privateOrb.statsDefInc())
                .healthPotential(privateOrb.statsHpInc())
                .statBonus(stat)
                .iconGraphicsUrl(graphicsServerUrl + GRAPHICS_SERVER_PATH + rawToFinalGraphicsIdMap.getOrDefault(privateOrb.iSd(), -1) + "/")
                .build();
    }

    private static ItemSlot covertItemSlotFromPrivate(byte privateItemType) {
        switch (privateItemType) {
            case EquipSlot.Head:
                return ItemSlot.HELM;
            case EquipSlot.Chest:
                return ItemSlot.CHEST;
            case EquipSlot.Hands:
                return ItemSlot.GLOVES;
            case EquipSlot.Legs:
                return ItemSlot.BOOTS;
            case EquipSlot.Amulet:
                return ItemSlot.NECKLACE;
            case EquipSlot.Ring:
                return ItemSlot.RING;
            case EquipSlot.Talisman:
                return ItemSlot.TALISMAN;
            case EquipSlot.MainHand:
                return ItemSlot.MAIN_HAND;
            case EquipSlot.OffHand:
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
                return Emblem.NONE;
        }
    }

    public static Quality convertQualityFromPrivate(byte privateQuality) {
        switch (privateQuality) {
            case ItemQuality.Common:
                return Quality.COMMON;
            case ItemQuality.Uncommon:
                return Quality.UNCOMMON;
            case ItemQuality.Rare:
                return Quality.RARE;
            case ItemQuality.Epic:
                return Quality.EPIC;
            case ItemQuality.Legendary:
                return Quality.LEGENDARY;
            case ItemQuality.LegendaryPlus:
                return Quality.ARTIFACT1;
            case ItemQuality.Artifact:
                return Quality.ARTIFACT2;
            case ItemQuality.ArtifactPlus:
                return Quality.ARTIFACT3;
            case ItemQuality.Relic:
                return Quality.ARTIFACT4;
            case ItemQuality.RelicPlus:
                return Quality.ARTIFACT5;
            default:
                throw new RuntimeException(
                        "Unable to parse quality for " + privateQuality + " while loading gear/item data");
        }
    }

    private static String convertToPassiveNameFromPrivate(int passiveId,
                                                          Map<Integer, PrivateWeaponPassive> weaponPassives) {
        PrivateWeaponPassive passive = weaponPassives.getOrDefault(passiveId, null);
        if (passive == null) {
            return null;
        }
        return passive.getName();
    }

    private static String convertToPassiveDescriptionFromPrivate(int passiveId,
                                                                 Map<Integer, PrivateWeaponPassive> weaponPassives) {
        PrivateWeaponPassive passive = weaponPassives.getOrDefault(passiveId, null);
        if (passive == null) {
            return null;
        }
        return cleanDescription(passive.getDescription(), passive.getTemplateData(),
                passive.getTriggerChance());
    }

    private static String cleanDescription(String description, String templateData,
                                           int triggerChance) {
        String[] attirbuteParts = templateData.split(",");

        return description
                .replace("<sprite=\"skill_atlas\" name=\"r\">", " red")
                .replace("<sprite=\"skill_atlas\" name=\"w\">", " white")
                .replace("<sprite=\"skill_atlas\" name=\"b\">", " blue")
                // Strip text formatting
                .replaceAll("\\r\\n•", "")
                .replaceAll("\\r\\n", "")

                // Handle template things
                .replace("#ATTR_OTHER#", arrayGetOrDefault(4, attirbuteParts) + "%")
                .replace("#TRIGGER_PARAM#", "" + triggerChance)
                .replace("#SHIELD#", arrayGetOrDefault(3, attirbuteParts) + "%")
                .replace("#HEAL#", arrayGetOrDefault(5, attirbuteParts) + "%")
                .replace("#CHAIN_CHANCE#", arrayGetOrDefault(1, attirbuteParts) + "%")
                .replace("#ATTR_CHANCE#", arrayGetOrDefault(6, attirbuteParts) + "%")

                // Hacky approach to strip html
                .replaceAll("\\<[^>]*>", "");
    }

    private static String arrayGetOrDefault(int index, String[] array) {
        if (array.length - 1 >= index) {
            return array[index];
        }
        return "";
    }

    private static Stat covertItemBonusFromPrivate(byte statType) {
        switch (statType) {
            case StatType.Attack:
                return Stat.ATTACK;
            case StatType.Defence:
                return Stat.DEFENSE;
            case StatType.Health:
                return Stat.HEALTH;
            case StatType.Magic:
                return Stat.MAGIC;
            default:
                return Stat.NONE;
        }
    }
}
