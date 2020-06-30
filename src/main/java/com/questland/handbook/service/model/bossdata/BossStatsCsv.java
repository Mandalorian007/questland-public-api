package com.questland.handbook.service.model.bossdata;

import java.math.BigInteger;
import lombok.Data;

@Data
public class BossStatsCsv {

  private BigInteger level;
  private BigInteger health;
  private BigInteger attack;
  private BigInteger defense;
  private BigInteger magic;
}
