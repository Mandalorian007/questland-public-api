package com.questland.handbook.publicmodel;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hero {

  private int id;
  private String server;
  private String guild;
  private String name;
  private int level;
  private int daysPlayed;
  private int vip;
  private int fame;
  private String language;
  private int heroPower;
  private int health;
  private int attack;
  private int defense;
  private int magic;
  private int critChance;
  private double critDmgMuti;
  private int dodgeChance;
  private int heroPowerRank;
  private int heroPvpRank;
  private CollectionSlots collection1Slots;
  private CollectionSlots collection2Slots;
  private List<BuildItem> equippedGear;
  private List<BuildItem> collections1;
  private List<BuildItem> collections2;
  private double battleEventMulti;
  private String row1Bonus;
  private String row2Bonus;
  private String row3Bonus;
  private String row4Bonus;
}
