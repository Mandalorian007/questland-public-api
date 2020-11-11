package com.questland.handbook.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.questland.handbook.controller.NotAuthorizedException;
import com.questland.handbook.service.model.GoogleProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleIdTokenVerifierService {
    private final GoogleIdTokenVerifier verifier;

    public GoogleIdTokenVerifierService(@Value("${GOOGLE_OAUTH_CLIENT_ID}") final String clientId) {
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(List.of(clientId))
                .build();
    }

    public GoogleProfile verify(String googleIdToken) throws NotAuthorizedException {
        try {
            GoogleIdToken idToken = verifier.verify(googleIdToken);

            if (idToken != null) {
                return GoogleProfile.builder()
                        .id(idToken.getPayload().getSubject())
                        .build();
            }

        } catch (Exception e) {
            throw new NotAuthorizedException();
        }
        throw new NotAuthorizedException();
    }
}
