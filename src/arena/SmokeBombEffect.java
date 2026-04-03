package com.arena;

public class SmokeBombEffect extends StatusEffect
{
    public SmokeBombEffect()
    {
        super(2);
    }

    @Override
    public void apply(Combatant combatant) {}

    @Override
    public String getName()
    {
        return "Smoke Bomb";
    }
}