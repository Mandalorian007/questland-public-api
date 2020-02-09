package com.questland.handbook;

import com.questland.handbook.model.Item;
import com.questland.handbook.model.Quality;
import java.util.List;
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
  public Item getItemByName(@PathVariable("name") String name,
                            @RequestParam(value = "quality", required = false) Quality quality) {
    List<Item> itemsByName = itemRepository.findByNameIgnoreCase(name);
    // This logic assists with filtering for specific artifacts
    if (quality != null) {
      itemsByName = itemsByName.stream()
          .filter(item -> item.getQuality().equals(quality))
          .collect(Collectors.toList());
      // This logic makes sure we select legendary over artifact if no quality was specified
    } else if (itemsByName.size() > 1) {
      itemsByName = itemsByName.stream()
          .filter(item -> item.getQuality().equals(Quality.LEGENDARY))
          .collect(Collectors.toList());
    }
    return itemsByName.stream().findFirst().orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Item was not found."));
  }

}