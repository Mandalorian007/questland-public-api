package com.questland.handbook.service.model.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
// Ignoring unknowns to minimize internal API reliance
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateSearchGuildData {
  @JsonProperty("guilds_list")
  private List<PrivateSearchGuildDetails> guildDetailList;
}
