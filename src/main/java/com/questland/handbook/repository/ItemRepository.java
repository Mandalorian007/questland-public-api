package com.questland.handbook.repository;

import com.questland.handbook.publicmodel.Item;
import com.questland.handbook.publicmodel.Quality;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

  List<Item> findAllByQualityInAndHiddenFalse(Set<Quality> qualities, Sort sort);

  List<Item> findAllByQualityIn(Set<Quality> qualities, Sort sort);

  List<Item> findByNameIgnoreCase(String name);
}
