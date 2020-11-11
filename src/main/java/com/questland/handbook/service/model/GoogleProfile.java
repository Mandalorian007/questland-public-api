package com.questland.handbook.service.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GoogleProfile {
    // https://developers.google.com/identity/sign-in/web/backend-auth
    private String id;
}
