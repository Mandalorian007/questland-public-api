package com.questland.handbook.publicmodel;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GuildRanking {
    private List<RankedGuild> rankings;
}
