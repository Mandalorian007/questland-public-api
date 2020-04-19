package com.questland.handbook.service.model.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateGuildDetails {
  private int id;
  private String name;
  private String desc;
  private int lvl;
  private int cnt;
  private int mcnt;
  private List<PrivateGuildMember> members;

}
