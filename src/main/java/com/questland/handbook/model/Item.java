package com.questland.handbook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    private long id;

    private long linkId;

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
    private Stat itemBonus;

    @ElementCollection
    private List<Integer> itemLinks;

    @Enumerated(EnumType.STRING)
    private Stat orbBonus;

    @ElementCollection
    private List<Integer> orbLinks;
}
