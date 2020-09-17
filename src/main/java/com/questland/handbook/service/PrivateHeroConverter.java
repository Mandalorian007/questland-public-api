package com.questland.handbook.service;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.loader.PrivateItemAndOrbConverter;
import com.questland.handbook.publicmodel.*;
import com.questland.handbook.repository.ItemRepository;
import com.questland.handbook.service.model.hero.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateHeroConverter {

    private final GuildService guildService;
    private final ItemRepository itemRepository;

    public Hero convertHeroFromPrivate(QuestlandServer server,
                                       PrivateProfileData privateProfileData) {
        PrivateProfile profile = privateProfileData.getProfile();
        String guildName = guildService.getGuildById(server, profile.getGuildId()).getName();

        PrivateAttributes attributes = profile.getAttributes();
        PrivatePvpDetails pvpDetails = privateProfileData.getPvpDetails();
        PrivateMultiplierDetailsContainer multiplierDetailsContainer =
                privateProfileData.getMultiplierDetails();
        PrivateMultiplierDetails multiplierDetails = multiplierDetailsContainer.getMultiplierDetails();
        PrivateBonusContainer row1 = null;
        PrivateBonusContainer row3 = null;
        PrivateBonusContainer row4 = null;
        if (multiplierDetails.getBonuses() != null) {
            row1 = multiplierDetails.getBonuses().getRow1();
            row3 = multiplierDetails.getBonuses().getRow3();
            row4 = multiplierDetails.getBonuses().getRow4();
        }

        Hero.HeroBuilder heroBuilder = Hero.builder()
                .id(profile.getId())
                .server(server.name())
                .guild(guildName)
                .name(profile.getName())
                .level(profile.getLevel())
                .daysPlayed(profile.getHeroAge())
                .vip(profile.getVipLevel())
                .fame(profile.getFameLevel())
                .language(profile.getLang())
                .heroPower(attributes.getHeropower())
                .health(attributes.getHp())
                .attack(attributes.getDmg())
                .defense(attributes.getDef())
                .magic(attributes.getMagic())
                .critChance(attributes.getCritchance())
                .critDmgMuti(attributes.getCritval())
                .dodgeChance(attributes.getDodge())
                //Note the rank starts with 0 so we add 1
                .heroPowerRank(privateProfileData.getHeroPowerRank().getRank() + 1)
                .heroPvpRank(getPvpRank(pvpDetails))
                .collection1Slots(convertCollections(profile.getCollection1()))
                .collection2Slots(convertCollections(profile.getCollection2()))
                .battleEventMulti(multiplierDetails.getMultiplier())
                .row1Bonus(getRowBonus(row1))
                .row2Bonus(getSpiritBonus(multiplierDetails.getSpiritBonus()))
                .row3Bonus(getRowBonus(row3))
                .row4Bonus(getRowBonus(row4));

        addPlayerGearInfo(privateProfileData.getProfileItems(), heroBuilder);

        return heroBuilder.build();
    }

    private String getRowBonus(PrivateBonusContainer row) {
        if (row != null) {
            PrivateBonus rowBonus = row.getBonus();
            if (rowBonus != null) {
                return rowBonus.getVal() + " " + rowBonus.getAttr().replaceAll("<.*?>", "");
            }
        }
        return null;
    }

    private String getSpiritBonus(PrivateSpiritBonus spiritBonus) {
        if (spiritBonus == null) {
            return null;
        }

        if (spiritBonus.getRed() > 0) {
            return spiritBonus.getRed() + " red";
        } else if (spiritBonus.getBlue() > 0) {
            return spiritBonus.getBlue() + " blue";
        }

        return null;
    }

    private int getPvpRank(PrivatePvpDetails pvpDetails) {
        PrivatePvpStatsContainer pvpStatsContainer = pvpDetails.getPvpStatsContainer();
        if (pvpStatsContainer != null) {
            PrivatePvpStats normal = pvpStatsContainer.getNormal();
            if (normal != null) {
                return normal.getPlace();
            }
        }
        return 0;
    }

    private void addPlayerGearInfo(List<PrivateProfileItem> items, Hero.HeroBuilder builder) {

        List<BuildItem> equippedGear = items.stream()
                .filter(item -> item.getWear() != null)
                .filter(item -> (Integer) item.getWear().get(0) == 1)
                .map(this::convertEquippedItem)
                .collect(Collectors.toList());

        List<PrivateProfileItem> privateOrbList = items.stream()
                .filter(item -> item.getOrb() != null)
                .filter(item -> item.getOrb().get(0) != null)
                .collect(Collectors.toList());

        attachOrbsToEquippedGear(equippedGear, privateOrbList);

        List<BuildItem> collections1 = items.stream()
                .filter(item -> item.getWear() != null)
                .filter(item -> (Integer) item.getWear().get(0) == 2)
                .map(this::convertEquippedItem)
                .collect(Collectors.toList());

        List<BuildItem> collections2 = items.stream()
                .filter(item -> item.getWear() != null)
                .filter(item -> (Integer) item.getWear().get(0) == 3)
                .map(this::convertEquippedItem)
                .collect(Collectors.toList());

        builder.equippedGear(equippedGear)
                .collections1(collections1)
                .collections2(collections2);
    }

    private BuildItem convertEquippedItem(PrivateProfileItem item) {
        BuildItem.BuildItemBuilder builder = BuildItem.builder();
        Integer id = (Integer) item.getA().get(1);
        List<Object> wear = item.getWear();
        Optional<Item> optionalItem = itemRepository.findById(Long.valueOf(id));
        if (optionalItem.isPresent()) {
            ItemSlot itemSlot = optionalItem.get().getItemSlot();
            builder = builder
                    .id(id)
                    .level((Integer) wear.get(1))
                    .boost((Integer) wear.get(2))
                    .itemSlot(itemSlot)
                    .collectionPosition((Integer) wear.get(5))
                    .healthReforge((Integer) wear.get(9))
                    .attackReforge((Integer) wear.get(7))
                    .defenseReforge((Integer) wear.get(8))
                    .magicReforge((Integer) wear.get(10));
        }
        return builder.build();
    }

    private void attachOrbsToEquippedGear(List<BuildItem> equippedGear,
                                          List<PrivateProfileItem> privateOrbList) {
        privateOrbList.forEach(profileItem -> {
            List<Object> orb = profileItem.getOrb();
            ItemSlot orbGearSlot = getDisplayableItemSlot((String) orb.get(0));
            BuildOrb buildOrb = BuildOrb.builder()
                    .id((Integer) profileItem.getA().get(1))
                    .level((Integer) orb.get(1))
                    .enhance((Integer) orb.get(2))
                    .build();

            Optional<BuildItem> gearWithThisOrb = equippedGear.stream()
                    .filter(gear -> gear.getItemSlot() == orbGearSlot)
                    .findFirst();

            //Note: If you un-equip a piece of gear the orbs are still equipped... somehow
            if (gearWithThisOrb.isPresent()) {
                BuildItem buildItem = gearWithThisOrb.get();
                List<BuildOrb> socketedOrbs = buildItem.getSocketedOrbs();
                if (socketedOrbs == null) {
                    socketedOrbs = new ArrayList<>();
                    buildItem.setSocketedOrbs(socketedOrbs);
                }

                socketedOrbs.add(buildOrb);
            }
        });
    }

    private CollectionSlots convertCollections(PrivateCollection collection) {
        return CollectionSlots.builder()
                .unlockedSlots(collection.getUnlockedSlots())
                .slotUpgradePercentages(collection.getSlotsUpgrade())
                .build();
    }

    private static ItemSlot getDisplayableItemSlot(String privateItemType) {
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
}