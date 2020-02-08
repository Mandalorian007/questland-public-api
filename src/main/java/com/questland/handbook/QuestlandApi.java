package com.questland.handbook;

import com.questland.handbook.model.Item;
import com.questland.handbook.model.Quality;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuestlandApi {

  private final ItemRepository itemRepository;

  @GetMapping("/items")
  public List<Item> getItems(Sort sort) {
    return itemRepository.findAll(sort);
  }

  @GetMapping("/items/{id}")
  public Item getItemById(@PathVariable("id") long id) {
    return itemRepository.findById(id).orElse(null);
  }

  @GetMapping("/items/name/{name}")
  public List<Item> getItemByName(@PathVariable("name") String name,
                                  @RequestParam(value = "", required = false) Quality quality) {
    List<Item> itemsByName = itemRepository.findByNameIgnoreCase(name);
    if (quality != null) {
      itemsByName = itemsByName.stream()
          .filter(item -> item.getQuality().equals(quality))
          .collect(Collectors.toList());
    }
    return itemsByName;
  }

}