package com.questland.handbook.publicmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeroPlan {

  private int id;
  private String name;
  private int heroPower;
  private int health;
  private int attack;
  private int defense;
  private int magic;
  private double battleEventMulti;
  private String row1Bonus;
  private String row2Bonus;
  private String row3Bonus;
  private String row4Bonus;
}
