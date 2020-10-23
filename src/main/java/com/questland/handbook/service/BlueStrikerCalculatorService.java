package com.questland.handbook.service;

import org.springframework.stereotype.Service;

@Service
public class BlueStrikerCalculatorService {
    private static final double MELEE_STAT_DEBUFF = .11;
    private static final double MAGIC_RESISTANCE = .1;
    /*
    Blue boss attacks:
    Melee: 100% w/ 20% shield ignore
    Magic: 90-110% with 80% shield ignore

    Build assumption:
    Oathgiver, Winged Defender
    Inner Fire, Chilling Cold, Mystical Wind
    Passive#2, 10% Magic Resistance, 4% healing

    Playstyle assumption:
    - stun resetting using two 1B to get more rounds
    - 6 round combat cycle after magic attack
    - 1B, 1B, 4B (stun), 4B boosted, 4B boosted, 4B boosted
     */

    /*
    Risky means we assume full health for each attack and full armor for melee as well as enough armor for magic. This requires a healing proc every 3 turns
     */
    public double blueGuildBossRiskyFormula(double heroHealth, double heroShield, double bossMagicStat, double bossAttackStat) {
        double heroCombatHealth = heroHealth * 5;
        double heroCombatShield = heroShield * 5;

        //Magic attack: assume enough shield must survive 110% damage with 80% direct to health
        double multiForMagic = (getBlueBossMagicDamage(bossMagicStat) * .8) / (heroCombatHealth - 1);

        //Melee attack: we can assume health won't go to 0 without shield going to zero so we can use total damage vs shield + health
        double multiForMelee = getBlueBossAttackDamage(bossAttackStat) / (heroCombatHealth + heroCombatShield - 1);

        //Return the higher multi requirement for survivability
        return Math.max(multiForMagic, multiForMelee);
    }

    /*
    Stable means a player needs to be able to survive 2 attacks without any mystical wind procs. As it's harder to heal up from a magic attack we will say the player must live from 1 magic then 1 melee
     */
    public double blueGuildBossStableFormula(double heroHealth, double heroShield, double innerFireLevel, double bossMagicStat, double bossAttackStat, double riskyMulti) {
        // We are going to assume the shield can absorb the damage and focus on the melee attack
        double blueGuildBossMagicDamage = getBlueBossMagicDamage(bossMagicStat) * .8;
        // Will assume this targets health + shield
        double blueGuildBossMeleeDamage = getBlueBossAttackDamage(bossAttackStat);
        boolean thresholdFound = false;
        double stableMulti = riskyMulti;

        while(!thresholdFound) {
            double heroCombatHealth = heroHealth * 5 * stableMulti;
            double heroCombatShield = heroShield * 5 * stableMulti;
            double sixRoundMultiHeal = heroCombatHealth * .04 * 6;
            double singleRoundSingleStackInnerFire = getAverageInnerFire1RHealPerStackPerRound(innerFireLevel) / 100 * heroCombatHealth;
            double innerFireHealApprox =  6 * singleRoundSingleStackInnerFire;

            // Calculate health after the magic attack:
            double playerRemainingHealth = heroCombatHealth - blueGuildBossMagicDamage;

            // apply all healing
            playerRemainingHealth = playerRemainingHealth + sixRoundMultiHeal + innerFireHealApprox;

            //test if player can survive the melee attack
            if(playerRemainingHealth + heroCombatShield < blueGuildBossMeleeDamage) {
                stableMulti++;
            } else {
                thresholdFound = true;
            }
        }
        return stableMulti;
    }

    /*
    Solo means a player needs to be able to survive 3 attacks without any mystical wind procs. As it's harder to heal up from a magic attack we will say the player must live from 1 magic, 1 melee, then 1 magic
     */
    public double blueGuildBossSoloKillFormula(double heroHealth, double heroShield, double innerFireLevel, double bossMagicStat, double bossAttackStat, double stableMulti) {
        // We are going to assume the shield can absorb the damage and focus on the melee attack
        double blueGuildBossMagicDamage = getBlueBossMagicDamage(bossMagicStat) * .8;
        // Will assume this targets health + shield
        double blueGuildBossMeleeDamage = getBlueBossAttackDamage(bossAttackStat);
        boolean thresholdFound = false;
        double soloMulti = stableMulti;

        while(!thresholdFound) {
            double heroCombatHealth = heroHealth * 5 * soloMulti;
            double heroCombatShield = heroShield * 5 * soloMulti;
            double sixRoundMultiHeal = heroCombatHealth * .04 * 6;
            double singleRoundSingleStackInnerFire = getAverageInnerFire1RHealPerStackPerRound(innerFireLevel) / 100 * heroCombatHealth;
            double innerFireSixRoundHealApprox =  6 * singleRoundSingleStackInnerFire;

            // Calculate health after the 1st magic attack:
            double playerRemainingHealth = heroCombatHealth - blueGuildBossMagicDamage;

            // apply all healing
            playerRemainingHealth = playerRemainingHealth + sixRoundMultiHeal + innerFireSixRoundHealApprox;

            //calculate health after melee attack
            double shieldOverflow = heroCombatShield - blueGuildBossMeleeDamage;
            // if the melee attack breaks the player's shield reduce their health (This should always happen)
            if(shieldOverflow < 0) {
                playerRemainingHealth += shieldOverflow;
            }

            // apply all healing
            playerRemainingHealth = playerRemainingHealth + sixRoundMultiHeal + innerFireSixRoundHealApprox;

            //test if player can survive the second magic attack
            if(playerRemainingHealth < blueGuildBossMagicDamage) {
                soloMulti++;
            } else {
                thresholdFound = true;
            }
        }
        return soloMulti;
    }

    private double getBlueBossMagicDamage(double bossMagicStat) {
        //There is currently no magic stat reduction so just straight resistance
        return bossMagicStat - (bossMagicStat * MAGIC_RESISTANCE);
    }

    private double getBlueBossAttackDamage(double bossAttackStat) {
        //There is no melee resistance
        return bossAttackStat - (bossAttackStat * MELEE_STAT_DEBUFF);
    }
    //5 - 7 base with 0.03 per level
    private double getAverageInnerFire1RHealPerStackPerRound(double innerFireLevel) {
        return 6.0 + (0.03 * innerFireLevel - 1);
    }

}