package com.questland.handbook.publicmodel;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GearSet {

    private final String refCode;
    private final String title;
    private final List<String> setsUsed;
    private final List<String> notes;
    private final String imageUrl;
}
