package com.arena;

public class DefendEffect extends StatusEffect
{
    private static final int DEFENSE_BONUS = 10;

    public DefendEffect()
    {
        super(2);
    }

    @Override
    public void apply(Combatant combatant)
    {
        if (duration == 2)
            combatant.defense += DEFENSE_BONUS;
    }

    @Override
    public void tick()
    {
        duration--;
        if (duration <= 0)
            duration = 0;
    }

    public void remove(Combatant combatant)
    {
        combatant.defense -= DEFENSE_BONUS;
    }

    @Override
    public String getName()
    {
        return "Defend";
    }
}