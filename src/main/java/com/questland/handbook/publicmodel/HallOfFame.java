package com.questland.handbook.publicmodel;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HallOfFame {
    private List<RankedHero> rankings;
}
