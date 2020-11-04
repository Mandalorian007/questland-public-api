package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.GearCollection;
import com.questland.handbook.service.GearCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GearCollectionsEndpoints {

    private final GearCollectionService gearCollectionService;

    @GetMapping("/gear-collections")
    public List<GearCollection> getMetaGearCollections() {
        return gearCollectionService.getGearCollections();
    }

    @GetMapping("/gear-collections/{refCode}")
    public GearCollection getSpecificMetaGearCollection(@PathVariable("refCode") String refCode) {
        return gearCollectionService.getSpecificGearCollection(refCode).orElse(null);

    }
}
