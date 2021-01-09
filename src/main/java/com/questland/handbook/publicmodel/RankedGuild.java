package com.questland.handbook.publicmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankedGuild {
    private int rank;
    private int guildId;
    private int guildScore;
    private String name;
}
