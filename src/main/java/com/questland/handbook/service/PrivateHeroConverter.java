package com.questland.handbook.service;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.loader.PrivateItemAndOrbConverter;
import com.questland.handbook.publicmodel.BuildItem;
import com.questland.handbook.publicmodel.BuildOrb;
import com.questland.handbook.publicmodel.CollectionSlots;
import com.questland.handbook.publicmodel.Hero;
import com.questland.handbook.publicmodel.ItemSlot;
import com.questland.handbook.repository.ItemRepository;
import com.questland.handbook.service.model.hero.PrivateAttributes;
import com.questland.handbook.service.model.hero.PrivateBonus;
import com.questland.handbook.service.model.hero.PrivateBonusContainer;
import com.questland.handbook.service.model.hero.PrivateCollection;
import com.questland.handbook.service.model.hero.PrivateMultiplierDetails;
import com.questland.handbook.service.model.hero.PrivateMultiplierDetailsContainer;
import com.questland.handbook.service.model.hero.PrivateProfile;
import com.questland.handbook.service.model.hero.PrivateProfileData;
import com.questland.handbook.service.model.hero.PrivateProfileItem;
import com.questland.handbook.service.model.hero.PrivatePvpDetails;
import com.questland.handbook.service.model.hero.PrivatePvpStats;
import com.questland.handbook.service.model.hero.PrivatePvpStatsContainer;
import com.questland.handbook.service.model.hero.PrivateSpiritBonus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    Integer id = (Integer) item.getA().get(1);
    List<Object> wear = item.getWear();
    ItemSlot itemSlot = itemRepository.findById(Long.valueOf(id)).get().getItemSlot();

    BuildItem.BuildItemBuilder itemBuilder = BuildItem.builder()
        .id(id)
        .level((Integer) wear.get(1))
        .boost((Integer) wear.get(2))
        .itemSlot(itemSlot)
        .collectionPosition((Integer) wear.get(5))
        .healthReforge((Integer) wear.get(9))
        .attackReforge((Integer) wear.get(7))
        .defenseReforge((Integer) wear.get(8))
        .magicReforge((Integer) wear.get(10));

    return itemBuilder.build();
  }

  private void attachOrbsToEquippedGear(List<BuildItem> equippedGear,
                                        List<PrivateProfileItem> privateOrbList) {
    privateOrbList.forEach(profileItem -> {
      List<Object> orb = profileItem.getOrb();
      ItemSlot orbGearSlot =
          PrivateItemAndOrbConverter.covertItemSlotFromPrivate((String) orb.get(0));
      BuildOrb buildOrb = BuildOrb.builder()
          .id((Integer) profileItem.getA().get(0))
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
}