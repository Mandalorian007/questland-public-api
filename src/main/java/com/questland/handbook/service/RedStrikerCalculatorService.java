package com.questland.handbook.service;

import org.springframework.stereotype.Service;

@Service
public class RedStrikerCalculatorService {
    private static final double MAGIC_STAT_DEBUFF = .1;
    private static final double MAGIC_RESISTANCE = .12;
    private static final double POWER_SPRINT_BONUS = 9;

    /*
    Risky simply means with no armor you can survive a single attack from the red boss
     */
    public double riskyFormula(double heroHealth, double bossMagicStat) {
        double heroCombatHealth = heroHealth * 5;
        //subtracting 1 from this so no matter what you should have 1 health left.
        return getRedGuildBossDamage(bossMagicStat) / (heroCombatHealth - 1);
    }

    /*
    Stable means that you can have a bad spirit board after receiving a boss attack and survive by using:
    1 blue, 1 unbuffed 4R and 1 buffed 4R
     */
    public double stableFormula(double heroHealth, double heroMagic, double transcendentalTornadoLevel, double bossMagicStat, double riskyMulti) {
        double redGuildBossDamage = getRedGuildBossDamage(bossMagicStat);
        boolean thresholdFound = false;
        double stableMulti = riskyMulti;

        while(!thresholdFound) {
            double heroCombatHealth = heroHealth * 5 * stableMulti;
            double heal1B = heroCombatHealth * getTranscendentalTornado1BluePercentHeal(transcendentalTornadoLevel);
            double healUnbuffed4R = heroMagic * .8 *stableMulti;
            double healBuffed4R = healUnbuffed4R * 4;
            double remainingHeroHealth = heroCombatHealth - redGuildBossDamage;

            if (remainingHeroHealth + heal1B + healUnbuffed4R + healBuffed4R < redGuildBossDamage) {
                stableMulti++;
            } else {
                thresholdFound = true;
            }

        }
        return stableMulti;
    }

    /*
    Solo killer means you can have 2 rounds of setup, 1 bad spirit, and 3 dodges and still kill the boss
    Additional assumption is the 15% attack link in your BE multiplier
    This means:
    24 rounds: 3 wasted (1B), 3 unbuffed, 3 dodged, 15 buffed
    50% even chance on 4W procs so roughly 8 landed procs without dodge

    power sprint: when your shield is over 50% you get bonus damage on your attacks
    - For this I will only factor bloodlust and I will only factor it on 1 attack unless your shield can survive the first boss hit, then the following 3 buffed attacks will get the bonus

    crits will only ever be calculated on a regular buffed attack to maintain a small error margin
    - 15 potential buffed attacks at 15% chance means 2.25 attacks. So we will factor 2 crits.

    this damage needs to kill the boss
     */
    public double soloKillFormula(double heroAttack, double heroDefense, double bloodlustLevel, double runicTouchLevel, double stableMulti, double bossHealth, double bossShield, double bossMagic) {
        double effectiveBossHealth = bossHealth + bossShield;
        double averageBloodlustPercent = getAverageBloodlustDamage4R(bloodlustLevel);
        double runicTouchMeleePercent = getRunicTouch4WMeleeDamage(runicTouchLevel);
        double runicTouchDefensePercent = getRunicTouch4WArmorDamage();

        boolean thresholdFound = false;
        double multiRequired = stableMulti;
        while (!thresholdFound) {
            //first 4R we have Power Sprint active and we are unbuffed
            int unbuffedPowerSpringBloodlustAttacks = 1;
            //there will be 1 unbuffed from post rez and 1 from spirit fix
            int unbuffedBloodlustAttacks = 2;

            // If the boss's first attack will drop our shield below 50% then we will only have 1 unbuffed power sprint attack.
            // If it cannot drop our shield that low we will get 3 additional buffed power sprint attacks
            int buffedPowerSpringBloodlustAttacks = 0;
            int buffedBloodlustAttacks = 13; //two of these are calculated as crits
            if (heroDefense * 5 * multiRequired / 2 > getRedGuildBossDamage(bossMagic) * .3) {
                buffedPowerSpringBloodlustAttacks =3;
                buffedBloodlustAttacks = 10;
            }

            double totalBossDamage = 0.0
                    + unbuffedPowerSpringBloodlustAttacks * getUnbuffedPowerSprintBloodlustDamage(heroAttack, averageBloodlustPercent, multiRequired)
                    + unbuffedBloodlustAttacks * getUnbuffedBloodlustDamage(heroAttack, averageBloodlustPercent, multiRequired)
                    + buffedPowerSpringBloodlustAttacks * getBuffedPowerSprintBloodlustDamage(heroAttack, averageBloodlustPercent, multiRequired)
                    + buffedBloodlustAttacks * getBuffedBloodlustDamage(heroAttack, averageBloodlustPercent, multiRequired)
                    + 2 * getBuffedCriticalBloodlustDamage(heroAttack, averageBloodlustPercent, multiRequired)
                    + 8 * getBuffedRunicTouchDamage(heroAttack, heroDefense, runicTouchMeleePercent, runicTouchDefensePercent, multiRequired);

            if(totalBossDamage < effectiveBossHealth) {
                multiRequired++;
            } else {
                thresholdFound = true;
            }
        }


        return multiRequired;
    }

    private double getRedGuildBossDamage(double bossMagicStat) {
        double bossMagicDamage = bossMagicStat - (bossMagicStat * MAGIC_STAT_DEBUFF);
        return bossMagicDamage - (bossMagicDamage * MAGIC_RESISTANCE);
    }

    private double getUnbuffedBloodlustDamage(double heroAttack, double bloodlustPercent, double multi) {
        return (heroAttack * ((multi + 15) / 100)) * bloodlustPercent;
    }

    private double getUnbuffedPowerSprintBloodlustDamage(double heroAttack, double bloodlustPercent, double multi) {
        return (heroAttack * ((multi + 15 + POWER_SPRINT_BONUS) / 100)) * bloodlustPercent;
    }

    private double getBuffedBloodlustDamage(double heroAttack, double bloodlustPercent, double multi) {
        return (heroAttack * ((multi + 215) / 100)) * bloodlustPercent;
    }

    private double getBuffedCriticalBloodlustDamage(double heroAttack, double bloodlustPercent, double multi) {
        return (heroAttack * ((multi + 215) / 100)) * bloodlustPercent * 1.5;
    }

    private double getBuffedPowerSprintBloodlustDamage(double heroAttack, double bloodlustPercent, double multi) {
        return (heroAttack * ((multi + 215 + POWER_SPRINT_BONUS) / 100)) * bloodlustPercent;
    }

    private double getBuffedRunicTouchDamage(double heroAttack, double heroDefense, double runicTouchAttackPercent, double runicTouchDefensePercent, double multi) {
        double attackComponent = (heroAttack * ((multi + 215) / 100)) * runicTouchAttackPercent;
        double defenseComponent = (heroDefense * ((multi + 15) / 100)) * runicTouchDefensePercent;
        return attackComponent + defenseComponent;
    }

    //4% base and .3 per level
    private double getTranscendentalTornado1BluePercentHeal(double transcendentalTornadoLevel) {
        return .04 + (.003 * (transcendentalTornadoLevel - 1));
    }

    //180 - 220 base with 7.5 per level
    private double getAverageBloodlustDamage4R(double bloodlustLevel) {
        return 200.0 + (7.5 * bloodlustLevel - 1);
    }

    //17 base with 1 per level
    private double getRunicTouch4WMeleeDamage(double runicTouchLevel) {
        return 17.0 + (1 * runicTouchLevel - 1);
    }

    //11 base none per level
    private double getRunicTouch4WArmorDamage() {
        return 11;
    }
}