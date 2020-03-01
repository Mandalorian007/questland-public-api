package com.questland.handbook.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageMob {
    private int id;

    private String name;

    private int count;
}
