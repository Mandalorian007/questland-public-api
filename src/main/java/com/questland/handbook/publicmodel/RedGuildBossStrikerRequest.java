package com.questland.handbook.publicmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RedGuildBossStrikerRequest {
    private int heroHealth;
    private int heroAttack;
    private int heroDefense;
    private int heroMagic;
    private int heroBloodlustLevel;
    private int heroTranscendentalTornadoLevel;
    private int heroRunicTouchLevel;
    private int bossLevel;
}
