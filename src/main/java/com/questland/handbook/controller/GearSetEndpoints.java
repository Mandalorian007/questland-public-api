package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.GearSet;
import com.questland.handbook.service.GearSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GearSetEndpoints {

    private final GearSetService gearSetService;

    @GetMapping("/gear-sets")
    public List<GearSet> getMetaGearSet() {
        return gearSetService.getGearSets();
    }

    @GetMapping("/gear-sets/{refCode}")
    public GearSet getSpecificMetaGearSets(@PathVariable("refCode") String refCode) {
        return gearSetService.getSpecificGearSet(refCode).orElse(null);

    }
}
