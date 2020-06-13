package com.questland.handbook.service.model.battlelocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateBattleLocation {
    private int id;

    @JsonProperty("order_by")
    private int number;

    private String name;

    private Map<Integer, PrivateStage> stages;
}
