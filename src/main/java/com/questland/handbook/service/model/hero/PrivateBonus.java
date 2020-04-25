package com.questland.handbook.service.model.hero;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateBonus {

  private int id;
  private String attr;
  private String val;
}
