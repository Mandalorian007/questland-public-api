package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateCollection {
  @JsonProperty("unlocked_slots")
  private int unlockedSlots;

  @JsonProperty("slots_upg")
  private List<Double> slotsUpgrade;
}
