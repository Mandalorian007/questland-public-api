package com.questland.handbook.service.model.battlelocation;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.questland.handbook.model.BattleLocation;
import com.questland.handbook.model.Stage;
import com.questland.handbook.model.StageMob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PrivateBattleLocationDetailsConverter {

    private List<MonsterCountCsv> monsterCountCache;

    public List<BattleLocation> convertFromPrivate(
            List<PrivateBattleLocation> privateBattleLocations,
            Map<Integer, PrivateMob> privateMobs) {

        return privateBattleLocations.stream()
                .filter(privateBattleLocation -> !privateBattleLocation.getName().endsWith("Campaign Event"))
                .map(privateBattleLocation -> getBattleLocationFromPrivate(privateBattleLocation, privateMobs))
                .collect(Collectors.toList());
    }

    private BattleLocation getBattleLocationFromPrivate(
            PrivateBattleLocation privateBattleLocation,
            Map<Integer, PrivateMob> privateMobs) {
        BattleLocation.BattleLocationBuilder battleLocationBuilder = BattleLocation.builder()
                .id(privateBattleLocation.getId())
                .name(privateBattleLocation.getName());

        List<Stage> stages = privateBattleLocation.getStages().values().stream()
                .map(privateStage -> getStageFromPrivate(privateStage, privateMobs))
                .collect(Collectors.toList());
        battleLocationBuilder.stages(stages);

        return battleLocationBuilder.build();
    }

    private Stage getStageFromPrivate(PrivateStage privateStage,
                                      Map<Integer, PrivateMob> privateMobs) {
        Stage.StageBuilder stageBuilder = Stage.builder()
                .id(privateStage.getId())
                .stageNumber(privateStage.getStageNumber())
                .name(privateStage.getName());

        privateMobs.forEach((mobId, privateMob) -> {
            if (privateMob.getStages().contains(privateStage.getId())) {
                stageBuilder.stageMob(getStageMobFromPrivate(privateMob, mobId, privateStage.getId()));
            }
        });

        return stageBuilder.build();
    }

    private StageMob getStageMobFromPrivate(PrivateMob privateMob, int mobId, int stageId) {
        Map<String, Integer> stageMonsterNameToCount = getMonsterCounts().stream()
                .filter(countCsv -> countCsv.getStageId() == stageId)
                .collect(Collectors.toMap(
                        MonsterCountCsv::getMonsterName,
                        MonsterCountCsv::getCount,
                        (count1, count2) -> {
                            if(!count1.equals(count2)) {
                                log.warn("Duplicate mob names had different counts and there might be an accuracy issue.");
                            }
                            return count1;
                        }));


        String trimedMobName = privateMob.getName().trim();
        return StageMob.builder()
                .id(mobId)
                .name(trimedMobName)
                .count(stageMonsterNameToCount.getOrDefault(trimedMobName, -1))
                .build();
    }

    private List<MonsterCountCsv> getMonsterCounts() {
        if (monsterCountCache == null) {
            try {
                CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
                CsvMapper mapper = new CsvMapper();
                File file = new ClassPathResource("monster-count.csv").getFile();
                MappingIterator<MonsterCountCsv> readValues =
                        mapper.readerFor(MonsterCountCsv.class).with(bootstrapSchema).readValues(file);
                monsterCountCache = readValues.readAll();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return monsterCountCache;
    }

}
