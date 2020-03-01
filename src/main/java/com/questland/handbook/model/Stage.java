package com.questland.handbook.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class Stage {

    private int id;

    private int stageNumber;

    private String name;

    @Singular
    private List<StageMob> stageMobs;
}
