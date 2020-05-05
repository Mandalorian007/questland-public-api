package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.Item;
import com.questland.handbook.publicmodel.Quality;
import com.questland.handbook.repository.ItemRepository;
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
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
public class ItemEndpoints {
  private final ItemRepository itemRepository;

  @GetMapping("/items")
  public List<Item> getItems(Sort sort,
                             @RequestParam(value = "filterArtifacts", defaultValue = "false") boolean filterArtifacts) {
    if (filterArtifacts) {
      return itemRepository.findAllByQualityInAndHiddenFalse(
          Set.of(
              Quality.COMMON,
              Quality.UNCOMMON,
              Quality.RARE,
              Quality.EPIC,
              Quality.LEGENDARY
          ),
          sort);
    } else {
      return itemRepository.findAll(sort);
    }
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

  @ApiIgnore
  @GetMapping("/developer-items")

  public List<Item> getItemsIncludingHidden(Sort sort,









































                             @RequestParam(value = "filterArtifacts", defaultValue = "false") boolean filterArtifacts) {
    if (filterArtifacts) {
      return itemRepository.findAllByQualityIn(
          Set.of(
              Quality.COMMON,
              Quality.UNCOMMON,
              Quality.RARE,
              Quality.EPIC,
              Quality.LEGENDARY
          ),
          sort);
    } else {
      return itemRepository.findAll(sort);
    }
  }

}
