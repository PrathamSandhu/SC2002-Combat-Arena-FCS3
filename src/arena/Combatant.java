package com.arena;

import java.util.ArrayList;
import java.util.List;

public abstract class Combatant
{
    protected int hp;
    protected int maxHp;
    protected int attack;
    protected int defense;
    protected int speed;
    protected List<StatusEffect> activeEffects;

    public Combatant(int hp, int attack, int defense, int speed)
    {
        this.hp = hp;
        this.maxHp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.activeEffects = new ArrayList<>();
    }

    public void takeDamage(int amount)
    {
        int damage = Math.max(0, amount - defense);
        hp = Math.max(0, hp - damage);
    }

    public void heal(int amount)
    {
        hp = Math.min(maxHp, hp + amount);
    }

    public void applyEffect(StatusEffect effect)
    {
        activeEffects.add(effect);
    }

    public void processEffects()
    {
        List<StatusEffect> expired = new ArrayList<>();
        for (StatusEffect effect : activeEffects)
        {
            effect.apply(this);
            effect.tick();
            if (effect.isExpired())
                expired.add(effect);
        }
        activeEffects.removeAll(expired);
    }

    public boolean isAlive()
    {
        return hp > 0;
    }

    public boolean hasEffect(Class<? extends StatusEffect> effectType)
    {
        for (StatusEffect effect : activeEffects)
        {
            if (effectType.isInstance(effect))
                return true;
        }
        return false;
    }

    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }
    public List<StatusEffect> getActiveEffects() { return activeEffects; }

    public abstract void takeTurn(List<Combatant> combatants, BattleEngine engine);
    public abstract String getName();
}