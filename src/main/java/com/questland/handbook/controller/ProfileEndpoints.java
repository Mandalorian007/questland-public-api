package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.GearTemplate;
import com.questland.handbook.publicmodel.Profile;
import com.questland.handbook.repository.GearTemplateRepository;
import com.questland.handbook.repository.ProfileRepository;
import com.questland.handbook.service.GoogleIdTokenVerifierService;
import com.questland.handbook.service.model.GoogleProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProfileEndpoints {
    private final ProfileRepository profileRepository;
    private final GoogleIdTokenVerifierService tokenVerifierService;
    private final GearTemplateRepository gearTemplateRepository;

    @GetMapping("/profile")
    public Profile login(@RequestHeader("Authorization") String authToken) {
        GoogleProfile googleProfile = tokenVerifierService.verify(authToken);
        String googleProfileId = googleProfile.getId();

        Optional<Profile> maybeProfile = profileRepository.findById(googleProfileId);
        // When your profile already exists
        if (maybeProfile.isPresent()) {
            Profile existingProfile = maybeProfile.get();
            // If your name, email, or profile image change update them
            if (existingProfile.getName().equals(googleProfile.getName())
                    && existingProfile.getEmail().equals(googleProfile.getEmail())
                    && existingProfile.getProfileImgUrl().equals(googleProfile.getProfileImage())) {
                return existingProfile;
            } else {
                existingProfile.setName(googleProfile.getName());
                existingProfile.setEmail(googleProfile.getEmail());
                existingProfile.setProfileImgUrl(googleProfile.getProfileImage());
                return profileRepository.save(existingProfile);
            }
        } else {
            // create the profile for a new user automatically
            Profile newProfile = Profile.builder()
                    .googleId(googleProfileId)
                    .name(googleProfile.getName())
                    .email(googleProfile.getEmail())
                    .profileImgUrl(googleProfile.getProfileImage())
                    .build();
            return profileRepository.save(newProfile);
        }
    }

    @PatchMapping("/profile")
    public Profile updateProfile(@RequestHeader("Authorization") String authToken, @RequestBody Profile updatedProfile) {
        GoogleProfile googleProfile = tokenVerifierService.verify(authToken);
        Profile profile = profileRepository.findById(googleProfile.getId()).orElseThrow(ResourceNotFoundException::new);

        if (updatedProfile.getDarkTheme() != null) {
            profile.setDarkTheme(updatedProfile.getDarkTheme());
        }
        return profileRepository.save(profile);
    }

    @DeleteMapping("/profile")
    public void removeProfile(@RequestHeader("Authorization") String authToken) {
        GoogleProfile googleProfile = tokenVerifierService.verify(authToken);

        // Clean up saved gear templates
        List<GearTemplate> savedGearTemplates = gearTemplateRepository.findByGoogleId(googleProfile.getId());
        if (savedGearTemplates.size() > 0) {
            gearTemplateRepository.deleteAll(savedGearTemplates);
        }

        // Clean up profile
        Profile profile = profileRepository.findById(googleProfile.getId()).orElseThrow(ResourceNotFoundException::new);
        profileRepository.delete(profile);
    }
}
