package com.questland.handbook.service;

import org.springframework.stereotype.Service;

@Service
public class BlueStrikerCalculatorService {
    private static final double MELEE_STAT_DEBUFF = .11;
    private static final double MAGIC_RESISTANCE = .1;

    /*
    Risky simply means we assume full health for each attack and full armor for melee as well as enough armor for magic.
     */
    public double blueGuildBossRiskyFormula(double heroHealth, double heroShield, double bossMagicStat, double bossAttackStat) {
        double heroCombatHealth = heroHealth * 5;
        double heroCombatShield = heroShield * 5;
        return 0.0;
    }

    /*

     */
    public double blueGuildBossStableFormula(double heroHealth, double heroMagic, double transcendentalTornadoLevel, double bossMagicStat, double riskyMulti) {
        return 0.0;
    }

    /*

     */
    public double blueGuildBossSoloKillFormula(double heroAttack, double heroDefense, double bloodlustLevel, double runicTouchLevel, double stableMulti, double bossHealth, double bossShield, double bossMagic) {
        return 0.0;
    }

}