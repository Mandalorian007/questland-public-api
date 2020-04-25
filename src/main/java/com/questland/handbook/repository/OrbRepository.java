package com.questland.handbook.repository;

import com.questland.handbook.publicmodel.Orb;
import com.questland.handbook.publicmodel.Quality;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrbRepository extends JpaRepository<Orb, Long> {

  List<Orb> findAllByQualityIn(Set<Quality> qualities, Sort sort);
  List<Orb> findByNameIgnoreCase(String name);
}
