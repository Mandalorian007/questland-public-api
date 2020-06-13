package com.questland.handbook.publicmodel;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonsterSlayerScore {

  private List<StageScore> stageScores;
}
