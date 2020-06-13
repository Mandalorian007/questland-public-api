package com.questland.handbook.publicmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageScore {
  private int locationNumber;
  private String locationName;
  private int stageNumber;
  private String stageName;
  private int stageScore;
}
