package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.OptimizedGearSet;
import com.questland.handbook.service.OptimizedGearSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GearSetEndpoints {

    private final OptimizedGearSetService optimizedGearSetService;

    @GetMapping("/optimized-gear-sets")
    public List<OptimizedGearSet> getOptimizedGearSets() {
        return optimizedGearSetService.getOptimizedGearSets();
    }

    @GetMapping("/optimized-gear-sets/{refCode}")
    public OptimizedGearSet getSpecificOptimizedGearSets(@PathVariable("refCode") String refCode) {
        return optimizedGearSetService.getOptimizedGearSet(refCode).orElse(null);
    }
}
