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
            performAttack(target);
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

    private void performAttack(Combatant target)
    {
        if (target.hasEffect(SmokeBombEffect.class))
        {
            System.out.println(getName() + " attacks " + target.getName() + " but Smoke Bomb blocks it!");
            return;
        }
        int damage = Math.max(0, attack - target.getDefense());
        // Bug fix: was takeDamage(attack), which ignored the defense calculation
        // entirely and dealt raw attack damage every time.
        target.takeDamage(damage);
        System.out.println(getName() + " attacks " + target.getName() + " for " + damage + " damage!");
    }

    @Override
    public abstract String getName();
}