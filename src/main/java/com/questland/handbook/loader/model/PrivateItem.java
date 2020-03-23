package com.questland.handbook.loader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateItem {

    @JsonProperty("i")
    private int id;

    @JsonProperty("q")
    private String quality;

    @JsonProperty("t")
    private int linkId;

    @JsonProperty("n")
    private String name;

    @JsonProperty("s")
    private String itemType;

    private int set;

    private PrivateStats stats;

    private List<PrivateLink> links;

    @JsonProperty("pskls")
    private List<Integer> weaponPassives;
}
