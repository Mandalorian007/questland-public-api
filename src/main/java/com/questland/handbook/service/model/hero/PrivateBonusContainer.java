package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateBonusContainer {

  private PrivateBonus bonus;

  /*
  The bonus is nested in a field named after the number of the option
  which we won't know ahead of time so we will bypass it and use the
  value instead.
   */
  @JsonAnySetter
  public void setBonusInfo(String name, PrivateBonus bonus) {
    this.bonus = bonus;
  }
}
