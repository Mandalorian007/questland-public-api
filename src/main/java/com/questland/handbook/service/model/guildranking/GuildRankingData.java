package com.questland.handbook.service.model.guildranking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class GuildRankingData {
    @JsonProperty("rankings_list_global_guild")
    private GuildRankingListWrapper rankingList;
}
