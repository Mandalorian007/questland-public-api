package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivatePvpDetails {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private PrivatePvpStatsContainer pvpStatsContainer;

  /*
  The actual json stores an object with the variable name of the heroId. Since we won't
  know the hero id in advance we are using the JsonAnySetter to grab the single field in
  this object and parse the reset of the object setting it directly to the pvpStatsContainer
  variable.
   */
  @JsonAnySetter
  public void setPvpStatsContainer(String name, PrivatePvpStatsContainer object) {
    pvpStatsContainer = object;
  }
}
