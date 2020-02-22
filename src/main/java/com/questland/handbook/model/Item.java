package com.questland.handbook.model;

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

    private int totalPotential;

    private int attack;

    private int magic;

    private int defense;

    private int health;

    private int attackPotential;

    private int magicPotential;

    private int defensePotential;

    private int healthPotential;

    @Enumerated(EnumType.STRING)
    private Emblem emblem;

    /*@Enumerated(EnumType.STRING)
    private Stat itemBonus;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Integer> itemLinks;

    @Enumerated(EnumType.STRING)
    private Stat orbBonus;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Integer> orbLinks;*/

    private String passive1Name;

    private String passive1Description;

    private String passive2Name;

    private String passive2Description;
}
