package com.questland.handbook;

import com.questland.handbook.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

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

}