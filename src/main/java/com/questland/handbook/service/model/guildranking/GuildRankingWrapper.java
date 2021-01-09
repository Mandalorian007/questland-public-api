package com.questland.handbook.service.model.guildranking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class GuildRankingWrapper {
    private GuildRankingData data;
}
