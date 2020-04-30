package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateMultiplierDetails {
  private double multiplier;
  private PrivateMultiplierLinkBonuses bonuses;
  @JsonProperty("spirit_bonus")
  private PrivateSpiritBonus spiritBonus;

}
