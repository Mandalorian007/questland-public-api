package com.questland.handbook.publicmodel;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BattleLocation {
    private int id;

    private int number;

    private String name;

    private List<Stage> stages;
}
