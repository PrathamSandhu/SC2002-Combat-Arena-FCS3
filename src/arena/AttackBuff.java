package com.arena;

public class AttackBuff extends StatusEffect
{
    private static final int ATTACK_BONUS = 10;

    public AttackBuff()
    {
        super(Integer.MAX_VALUE);
    }

    @Override
    public void apply(Combatant combatant)
    {
        if (duration == Integer.MAX_VALUE)
            combatant.attack += ATTACK_BONUS;
    }

    public void remove(Combatant combatant)
    {
        combatant.attack -= ATTACK_BONUS;
    }

    @Override
    public String getName()
    {
        return "Attack Buff";
    }
}