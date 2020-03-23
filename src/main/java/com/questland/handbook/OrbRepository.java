package com.questland.handbook;

import com.questland.handbook.model.Orb;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrbRepository extends JpaRepository<Orb, Long> {

  List<Orb> findByNameIgnoreCase(String name);

}
