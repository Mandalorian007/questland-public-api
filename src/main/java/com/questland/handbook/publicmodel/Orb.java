package com.questland.handbook.publicmodel;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orb implements Persistable<Long> {

    @Id
    private long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Quality quality;

    @Enumerated(EnumType.STRING)
    private Stat statBonus;

    private int attack;

    private int magic;

    private int defense;

    private int health;

    private int attackPotential;

    private int magicPotential;

    private int defensePotential;

    private int healthPotential;

    private String iconGraphicsUrl;

    //prevent Spring Data doing a select-before-insert - this particular entity is never updated
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true;
    }

}
