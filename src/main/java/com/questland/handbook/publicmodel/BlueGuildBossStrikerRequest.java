package com.questland.handbook.publicmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlueGuildBossStrikerRequest {
    private int heroHealth;
    private int heroAttack;
    private int heroDefense;
    private int heroMagic;
    private int heroInnerFireLevel;
    private int heroTChillingColdLevel;
    private int heroMysticalWindLevel;
    private int bossLevel;
}
