package com.questland.handbook.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.questland.handbook.publicmodel.BossStats;
import com.questland.handbook.service.model.bossdata.BossStatsCsv;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BossDataService {

  private Map<BigInteger, BossStats> guildBossStatsCache;
  private Map<BigInteger, BossStats> hardBossStatsCache;

  public Map<BigInteger, BossStats> getGuildBossStats() {
    if (guildBossStatsCache != null) {
      return guildBossStatsCache;
    }
    Map<BigInteger, BossStats> guildBossStats = loadBossStats("guild-boss-stats.csv").stream()
        .collect(Collectors.toMap(BossStatsCsv::getLevel, this::convert));
    guildBossStatsCache = guildBossStats;
    return guildBossStats;
  }

  public Map<BigInteger, BossStats> getHardBossStats() {
    if (hardBossStatsCache != null) {
      return hardBossStatsCache;
    }
    Map<BigInteger, BossStats> hardBossStats = loadBossStats("hard-boss-stats.csv").stream()
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

  private List<BossStatsCsv> loadBossStats(String resourceFileName) {
    try {
      CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
      CsvMapper mapper = new CsvMapper();
      InputStream hbStats = new ClassPathResource(resourceFileName).getInputStream();
      MappingIterator<BossStatsCsv> readValues =
              mapper.readerFor(BossStatsCsv.class).with(bootstrapSchema).readValues(hbStats);
      return readValues.readAll();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
