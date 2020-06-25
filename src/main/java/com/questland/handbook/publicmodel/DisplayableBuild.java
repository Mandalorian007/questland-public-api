package com.questland.handbook.publicmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DisplayableBuild {
  @JsonIgnore
  private Build build;
  private String name;
  private String description;
  private String weapons;
  private String mainHandAlternatives;
  private String offhandAlternatives;
  private String talent1;
  private String talent2;
  private String talent3;
  private String links;
  private String videoGuide;
  private String image;
}
