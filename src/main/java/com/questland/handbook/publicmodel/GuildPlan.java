package com.questland.handbook.publicmodel;


import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuildPlan {

  private int guildId;
  private String name;
  private List<HeroPlan> heroPlans;
}
