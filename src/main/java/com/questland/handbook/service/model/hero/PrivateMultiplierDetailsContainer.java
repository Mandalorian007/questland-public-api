package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateMultiplierDetailsContainer {
  private PrivateMultiplierDetails multiplierDetails;

  /*
  This json object will be keyed by the heroId which we won't know in advance
  of parsing so we will be bypassing the name of that field and setting it directly
   */
  @JsonAnySetter
  public void setMutliplierDetails(String name, PrivateMultiplierDetails privateMultiplierDetails) {
    multiplierDetails = privateMultiplierDetails;
  }
}
