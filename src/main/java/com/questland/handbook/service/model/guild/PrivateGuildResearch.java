package com.questland.handbook.service.model.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateGuildResearch {

  private List<Integer> damage;
  private List<Integer> defense;
  private List<Integer> hp;
  private List<Integer> magic;
}
