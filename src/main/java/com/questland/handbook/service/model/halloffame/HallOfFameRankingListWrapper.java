package com.questland.handbook.service.model.halloffame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class HallOfFameRankingListWrapper {
    private HallOfFameLadderEntry[] ladder;
}
