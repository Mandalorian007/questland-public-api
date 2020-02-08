package com.questland.handbook;

import com.questland.handbook.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

  List<Item> findByNameIgnoreCase(String name);
}
