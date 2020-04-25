package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.Orb;
import com.questland.handbook.publicmodel.Quality;
import com.questland.handbook.repository.OrbRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class OrbEndpoints {
  private final OrbRepository orbRepository;

  @GetMapping("/orbs")
  public List<Orb> getOrbs(Sort sort,
                           @RequestParam(value = "filterArtifacts", defaultValue = "false") boolean filterArtifacts) {
    if (filterArtifacts) {
      return orbRepository.findAllByQualityIn(
          Set.of(
              Quality.COMMON,
              Quality.UNCOMMON,
              Quality.RARE,
              Quality.EPIC,
              Quality.LEGENDARY
          ),
          sort);
    } else {
      return orbRepository.findAll(sort);
    }
  }

  @GetMapping("/orbs/{id}")
  public Orb getOrbById(@PathVariable("id") long id) {
    return orbRepository.findById(id).orElse(null);
  }

  @GetMapping("/orbs/name/{name}")
  public Orb getOrbByName(@PathVariable("name") String name,
                          @RequestParam(value = "quality", required = false) Quality quality) {
    List<Orb> orbsByName = orbRepository.findByNameIgnoreCase(name);
    // This logic assists with filtering for specific artifacts
    if (quality != null) {
      orbsByName = orbsByName.stream()
          .filter(orb -> orb.getQuality().equals(quality))
          .collect(Collectors.toList());
      // This logic makes sure we select legendary over artifact if no quality was specified
    } else if (orbsByName.size() > 1) {
      orbsByName = orbsByName.stream()
          .filter(orb -> orb.getQuality().equals(Quality.LEGENDARY))
          .collect(Collectors.toList());
    }
    return orbsByName.stream().findFirst().orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Orb was not found."));
  }

}
