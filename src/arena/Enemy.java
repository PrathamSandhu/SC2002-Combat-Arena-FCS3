package arena;

import java.util.List;

public abstract class Enemy extends Combatant
{
    public Enemy(int hp, int attack, int defense, int speed)
    {
        super(hp, attack, defense, speed);
    }

    @Override
    public void takeTurn(List<Combatant> combatants, BattleEngine engine)
    {
        Combatant target = getTarget(combatants);
        if (target != null)
            performAttack(target, engine);
    }

    private Combatant getTarget(List<Combatant> combatants)
    {
        for (Combatant c : combatants)
        {
            if (c instanceof Player && c.isAlive())
                return c;
        }
        return null;
    }

    private void performAttack(Combatant target, BattleEngine engine)
    {
        if (target.hasEffect(SmokeBombEffect.class))
        {
            String record = String.format("%s -> BasicAttack -> %s: 0 damage (Smoke Bomb active) | %s HP: %d",
                    getName(), target.getName(), target,getName(), target,getHp());
            engine.log(record);
            return;
        }
        int damage = Math.max(0, attack - target.getDefense());
        int initHp = target.getHp();
        target.takeDamage(damage);
        engine.log(String.format("%s -> BasicAttack -> %s: HP: %d -> %d (dmg: %d - %d = %d)",
            getName(), target.getName(), initHp, target.getHp(),
            attack, target.getDefense(), damage));
    }

    @Override
    public abstract String getName();
}