package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.GearTemplate;
import com.questland.handbook.publicmodel.OptimizedGearTemplate;
import com.questland.handbook.repository.GearTemplateRepository;
import com.questland.handbook.service.GoogleIdTokenVerifierService;
import com.questland.handbook.service.OptimizedGearTemplateService;
import com.questland.handbook.service.model.GoogleProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GearTemplateEndpoints {

    private final OptimizedGearTemplateService optimizedGearTemplateService;
    private final GearTemplateRepository gearTemplateRepository;
    private final GoogleIdTokenVerifierService tokenVerifierService;

    @GetMapping("/gear-templates")
    public List<GearTemplate> getGearSets(@RequestHeader("Authorization") String authToken) {
        GoogleProfile googleProfile = tokenVerifierService.verify(authToken);

        return gearTemplateRepository.findByGoogleId(googleProfile.getId());
    }

    @GetMapping("/gear-templates/{id}")
    public GearTemplate getGearSets(@PathVariable("id") UUID gearSetId) {
        return gearTemplateRepository.findById(gearSetId).orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping("/gear-templates")
    public GearTemplate saveGearSet(@RequestHeader("Authorization") String authToken, @Valid @RequestBody GearTemplate gearTemplate) {
        GoogleProfile googleProfile = tokenVerifierService.verify(authToken);

        gearTemplate.setGoogleId(googleProfile.getId());
        return gearTemplateRepository.save(gearTemplate);
    }

    @DeleteMapping("/gear-templates/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteGearSet(@RequestHeader("Authorization") String authToken, @PathVariable("id") UUID gearSetId) {
        tokenVerifierService.verify(authToken);
        gearTemplateRepository.deleteById(gearSetId);
    }

    @GetMapping("/optimized-gear-templates")
    public List<OptimizedGearTemplate> getOptimizedGearSets() {
        return optimizedGearTemplateService.getOptimizedGearTemplates();
    }

    @GetMapping("/optimized-gear-templates/{refCode}")
    public OptimizedGearTemplate getSpecificOptimizedGearSets(@PathVariable("refCode") String refCode) {
        return optimizedGearTemplateService.getOptimizedGearTemplate(refCode).orElse(null);
    }
}
