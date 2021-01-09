package com.questland.handbook.service.model.halloffame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class HallOfFameLadderEntry {
    @JsonProperty("hero_id")
    private int heroId;
    private String name;
    @JsonProperty("power")
    private int heroPower;
    @JsonProperty("score")
    private int rank;
}
