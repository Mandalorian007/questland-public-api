package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.Profile;
import com.questland.handbook.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;

@ApiIgnore
@RestController
@RequiredArgsConstructor
public class ProfileEndpoints {
    private final ProfileRepository profileRepository;

    @PostMapping("/profile")
    public void login(@RequestBody Profile profile) {
        Optional<Profile> existingProfile = profileRepository.findById(profile.getGoogleId());
        // consider updating the profile in the future if it does exist
        if (existingProfile.isEmpty()) {
            profileRepository.save(profile);
        }
    }

    @GetMapping("/profile")
    public List<Profile> getProfiles() {
        return List.of(
                new Profile(123)
        );
    }
}
