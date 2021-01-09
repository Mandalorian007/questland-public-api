package com.questland.handbook.publicmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankedHero {
    private int rank;
    private int heroId;
    private int heroPower;
    private String name;
}
