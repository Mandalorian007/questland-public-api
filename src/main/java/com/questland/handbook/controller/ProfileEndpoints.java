package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.Profile;
import com.questland.handbook.repository.ProfileRepository;
import com.questland.handbook.service.GoogleIdTokenVerifierService;
import com.questland.handbook.service.model.GoogleProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

@ApiIgnore
@RestController
@RequiredArgsConstructor
public class ProfileEndpoints {
    private final ProfileRepository profileRepository;
    private final GoogleIdTokenVerifierService tokenVerifierService;

    @PostMapping("/profile")
    public void login(@RequestHeader("id_token") String googleIdToken) {
        GoogleProfile googleProfile = tokenVerifierService.verify(googleIdToken);
        String googleProfileId = googleProfile.getId();
        Optional<Profile> existingProfile = profileRepository.findById(googleProfileId);
        // consider updating the profile in the future if it does exist
        if (existingProfile.isEmpty()) {
            profileRepository.save(Profile.builder().googleId(googleProfileId).build());
        }
    }

    @GetMapping("/profile")
    public Profile getProfile(@RequestHeader("id_token") String googleIdToken) {
        GoogleProfile googleProfile = tokenVerifierService.verify(googleIdToken);

        return profileRepository.findById(googleProfile.getId()).orElseThrow(ResourceNotFoundException::new);
    }
}
