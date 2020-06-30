package com.questland.handbook.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.questland.handbook.publicmodel.BossStats;
import com.questland.handbook.service.model.bossdata.BossStatsCsv;
import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class BossDataService {

  private Map<BigInteger, BossStats> guildBossStatsCache;
  private Map<BigInteger, BossStats> hardBossStatsCache;

  public Map<BigInteger, BossStats> getGuildBossStats() {
    if (guildBossStatsCache != null) {
      return guildBossStatsCache;
    }
    Map<BigInteger, BossStats> guildBossStats = loadGuildBossStats().stream()
        .collect(Collectors.toMap(BossStatsCsv::getLevel, this::convert));
    guildBossStatsCache = guildBossStats;
    return guildBossStats;
  }

  public Map<BigInteger, BossStats> getHardBossStats() {
    if (guildBossStatsCache != null) {
      return guildBossStatsCache;
    }
    Map<BigInteger, BossStats> hardBossStats = loadHardBossStats().stream()
        .collect(Collectors.toMap(BossStatsCsv::getLevel, this::convert));
    hardBossStatsCache = hardBossStats;
    return hardBossStats;
  }

  private BossStats convert(BossStatsCsv csv) {
    BossStats stats = new BossStats();
    stats.setHealth(csv.getHealth());
    stats.setAttack(csv.getAttack());
    stats.setDefense(csv.getDefense());
    stats.setMagic(csv.getMagic());

    return stats;
  }

  private List<BossStatsCsv> loadGuildBossStats() {
    try {
      CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
      CsvMapper mapper = new CsvMapper();
      File file = new ClassPathResource("guild-boss-stats.csv").getFile();
      MappingIterator<BossStatsCsv> readValues =
          mapper.readerFor(BossStatsCsv.class).with(bootstrapSchema).readValues(file);
      return readValues.readAll();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private List<BossStatsCsv> loadHardBossStats() {
    try {
      CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
      CsvMapper mapper = new CsvMapper();
      File file = new ClassPathResource("hard-boss-stats.csv").getFile();
      MappingIterator<BossStatsCsv> readValues =
          mapper.readerFor(BossStatsCsv.class).with(bootstrapSchema).readValues(file);
      return readValues.readAll();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
