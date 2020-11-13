package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.Profile;
import com.questland.handbook.repository.ProfileRepository;
import com.questland.handbook.service.GoogleIdTokenVerifierService;
import com.questland.handbook.service.model.GoogleProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProfileEndpoints {
    private final ProfileRepository profileRepository;
    private final GoogleIdTokenVerifierService tokenVerifierService;

    @GetMapping("/profile")
    public Profile login(@RequestHeader("id_token") String googleIdToken) {
        GoogleProfile googleProfile = tokenVerifierService.verify(googleIdToken);
        String googleProfileId = googleProfile.getId();

        Optional<Profile> existingProfile = profileRepository.findById(googleProfileId);
        if (existingProfile.isPresent()) {
            return existingProfile.get();
        } else {
            // create the profile for a new user automatically
            Profile newProfile = Profile.builder().googleId(googleProfileId).build();
            return profileRepository.save(newProfile);
        }
    }

    @PatchMapping("/profile")
    public Profile updateProfile(@RequestHeader("id_token") String googleIdToken, @RequestBody Profile updatedProfile) {
        GoogleProfile googleProfile = tokenVerifierService.verify(googleIdToken);
        Profile profile = profileRepository.findById(googleProfile.getId()).orElseThrow(ResourceNotFoundException::new);

        if (updatedProfile.getDarkTheme() != null) {
            profile.setDarkTheme(updatedProfile.getDarkTheme());
        }
        return profileRepository.save(profile);
    }
}
