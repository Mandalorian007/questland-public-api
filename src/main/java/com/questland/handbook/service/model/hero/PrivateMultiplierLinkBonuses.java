package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateMultiplierLinkBonuses {

  @JsonProperty("1")
  private PrivateBonusContainer row1;

  @JsonProperty("3")
  private PrivateBonusContainer row3;

  @JsonProperty("4")
  private PrivateBonusContainer row4;
}
