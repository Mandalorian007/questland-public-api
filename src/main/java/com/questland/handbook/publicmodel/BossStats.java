package com.questland.handbook.publicmodel;

import java.math.BigInteger;
import lombok.Data;

@Data
public class BossStats {

  private BigInteger health;
  private BigInteger attack;
  private BigInteger defense;
  private BigInteger magic;
}
