package com.questland.handbook.model;

import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    private long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Quality quality;

    @Enumerated(EnumType.STRING)
    private ItemSlot itemSlot;

    private int potential;

    private int attack;

    private int magic;

    private int defense;

    private int health;

    @Enumerated(EnumType.STRING)
    private Emblem emblem;

    @Enumerated(EnumType.STRING)
    private Stat itemBonus;

    @ElementCollection
    private List<Integer> itemLinks;

    @Enumerated(EnumType.STRING)
    private Stat orbBonus;

    @ElementCollection
    private List<Integer> orbLinks;
}
