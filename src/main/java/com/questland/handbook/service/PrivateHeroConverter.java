package com.questland.handbook.service;

import com.questland.handbook.config.QuestlandServer;
import com.questland.handbook.publicmodel.Hero;
import com.questland.handbook.service.model.hero.PrivateAttributes;
import com.questland.handbook.service.model.hero.PrivateBonus;
import com.questland.handbook.service.model.hero.PrivateBonusContainer;
import com.questland.handbook.service.model.hero.PrivateMultiplierDetails;
import com.questland.handbook.service.model.hero.PrivateMultiplierDetailsContainer;
import com.questland.handbook.service.model.hero.PrivateProfile;
import com.questland.handbook.service.model.hero.PrivateProfileData;
import com.questland.handbook.service.model.hero.PrivatePvpDetails;
import com.questland.handbook.service.model.hero.PrivatePvpStats;
import com.questland.handbook.service.model.hero.PrivatePvpStatsContainer;
import com.questland.handbook.service.model.hero.PrivateSpiritBonus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrivateHeroConverter {

  private final GuildService guildService;

  public Hero convertHeroFromPrivate(QuestlandServer server,
                                     PrivateProfileData privateProfileData) {
    PrivateProfile profile = privateProfileData.getProfile();
    String guildName = guildService.getGuildById(server, profile.getGuildId()).getName();

    PrivateAttributes attributes = profile.getAttributes();
    PrivatePvpDetails pvpDetails = privateProfileData.getPvpDetails();
    PrivateMultiplierDetailsContainer multiplierDetailsContainer =
        privateProfileData.getMultiplierDetails();
    PrivateMultiplierDetails multiplierDetails = multiplierDetailsContainer.getMultiplierDetails();
    PrivateBonusContainer row1 = multiplierDetails.getBonuses().getRow1();
    PrivateBonusContainer row3 = multiplierDetails.getBonuses().getRow3();
    PrivateBonusContainer row4 = multiplierDetails.getBonuses().getRow4();

    return Hero.builder()
        .id(profile.getId())
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
        .battleEventMulti(multiplierDetails.getMultiplier())
        .row1Bonus(getRowBonus(row1))
        .row2Bonus(getSpiritBonus(multiplierDetails.getSpiritBonus()))
        .row3Bonus(getRowBonus(row3))
        .row4Bonus(getRowBonus(row4))
        .build();
  }

  private String getRowBonus(PrivateBonusContainer row) {
    if (row != null) {
      PrivateBonus rowBonus = row.getBonus();
      if (rowBonus != null) {
        return rowBonus.getVal() + " " + rowBonus.getAttr().replaceAll("<.*?>", "");
      }
    }
    return "";
  }

  private String getSpiritBonus(PrivateSpiritBonus spiritBonus) {
    if (spiritBonus.getRed() > 0) {
      return spiritBonus.getRed() + " red";
    } else if (spiritBonus.getBlue() > 0) {
      return spiritBonus.getBlue() + " blue";
    } else {
      return "No Spirit Bonus";
    }
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
}