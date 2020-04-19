package com.questland.handbook.service.model.battlelocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateStage {
    private int id;

    @JsonProperty("battle_location_id")
    private int battleLocationId;

    @JsonProperty("stage_no")
    private int stageNumber;

    private String name;
}
