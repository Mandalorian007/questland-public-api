package com.questland.handbook.service.model.halloffame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class HallOfFameData {
    @JsonProperty("rankings_list")
    private HallOfFameRankingListWrapper[] rankingList;
}
