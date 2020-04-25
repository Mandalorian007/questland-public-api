package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateProfileData {
  private PrivateProfile profile;
  @JsonProperty("profile_heropower_rank")
  private PrivateHeroPowerRank heroPowerRank;
  @JsonProperty("profile_items_list")
  private List<PrivateProfileItem> profileItems;
  @JsonProperty("profile_multiply_slots")
  private PrivateMultiplierDetailsContainer multiplierDetails;
  @JsonProperty("profile_pvp")
  private PrivatePvpDetails pvpDetails;
}
