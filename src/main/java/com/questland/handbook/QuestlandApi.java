package com.questland.handbook;

import com.questland.handbook.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuestlandApi {
    private final ItemRepository itemRepository;

    @GetMapping("/items")
    public List<Item> getItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/items/{linkId}")
    public Item getItemByLinkId(@PathVariable("linkId") long linkId) {
        return itemRepository.findByLinkId(linkId);
    }
}