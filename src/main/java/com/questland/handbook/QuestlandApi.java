package com.questland.handbook;

import com.questland.handbook.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuestlandApi {
    private final ItemRepository itemRepository;

    @GetMapping("/items")
    public Page<Item> getItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    @GetMapping("/items/{linkId}")
    public Item getItemByLinkId(@PathVariable("linkId") long linkId) {
        return itemRepository.findByLinkId(linkId);
    }

}