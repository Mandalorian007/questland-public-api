package com.questland.handbook;

import com.questland.handbook.model.Orb;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrbRepository extends JpaRepository<Orb, Long> {

  Orb findByNameIgnoreCase(String name);

}
