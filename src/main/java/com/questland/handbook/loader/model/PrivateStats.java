package com.questland.handbook.loader.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PrivateStats {

    @JsonProperty("dmg")
    private int [] attack;

    private int [] magic;

    @JsonProperty("def")
    private int [] defense;

    @JsonProperty("hp")
    private int [] health;
}
