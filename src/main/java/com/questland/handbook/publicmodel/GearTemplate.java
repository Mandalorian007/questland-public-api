package com.questland.handbook.publicmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GearTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    @JsonIgnore
    private String googleId;

    private String name;

    private Long helm;
    private Long chest;
    private Long gloves;
    private Long boots;
    private Long necklace;
    private Long ring;
    private Long talisman;

    @NotNull
    @Size(min=5, max=5)
    @ElementCollection
    private List<Long> healthCollections;
    @NotNull
    @Size(min=5, max=5)
    @ElementCollection
    private List<Long> attackCollections;
    @NotNull
    @Size(min=5, max=5)
    @ElementCollection
    private List<Long> defenseCollections;
    @NotNull
    @Size(min=5, max=5)
    @ElementCollection
    private List<Long> magicCollections;
}