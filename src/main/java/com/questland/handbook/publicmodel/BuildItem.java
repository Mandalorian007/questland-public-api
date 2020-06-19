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
public class BuildItem {
  private int id;
  private int level;
  private int boost;
  private ItemSlot itemSlot;
  private int collectionPosition;
  private int healthReforge;
  private int attackReforge;
  private int defenseReforge;
  private int magicReforge;
  private List<BuildOrb> socketedOrbs;
}
