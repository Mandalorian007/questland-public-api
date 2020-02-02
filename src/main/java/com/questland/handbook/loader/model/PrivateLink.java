package com.questland.handbook.loader.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PrivateLink {

    @JsonProperty("e")
    private String bonusStat;

    @JsonProperty("p")
    private int bonusAmount;

    @JsonProperty("i")
    private int [][] linkData;


}
