package arena;

public class ArcaneBlast extends SpecialSkill
{
    // new
    @Override
    public String execute(Combatant user, Combatant target, BattleEngine engine, boolean ignoreCoolDown)
    {
        if (!ignoreCoolDown && !isUsable())
        {
            // Bug fix: was "ShieldBash on cooldown!" (copy-paste error)
            return "ArcaneBlast on cooldown!";
        }

        StringBuilder message = new StringBuilder();
        message.append(String.format("%s -> %s -> All Enemies:%n", user.getName(), getName()));

        // Bug fix: triggerCoolDown() was called once per enemy inside the loop,
        // then again after the loop. It only needs to be called once total.
        triggerCoolDown();

        for (Combatant enemy : engine.getEnemies())
        {
            int damage = Math.max(0, user.getAttack() - enemy.getDefense());
            int initHp = enemy.getHp();
            enemy.takeDamage(damage);
            if (!target.isAlive()) {
                engine.registerElimination(target);
            }

            if (enemy.getHp() > 0)
            {
                message.append(String.format(
                    "  %s: HP: %d -> %d (dmg: %d - %d = %d)%n",
                    enemy.getName(), initHp, enemy.getHp(),
                    user.getAttack(), enemy.getDefense(), damage));
            }
            else
            {
                // Bug fix: previously called atkBuff.apply(user) manually before
                // user.applyEffect(atkBuff), causing the +10 to fire immediately AND
                // again on every future processEffects() call (infinite stacking).
                // Now we only call applyEffect() — the effect system handles apply().
            	int preAttack = user.getAttack();
                AttackBuff atkBuff = new AttackBuff();
                user.applyEffect(atkBuff);
                atkBuff.apply(user);
                message.append(String.format(
                    "  %s: HP: %d -> %d ELIMINATED (dmg: %d - %d = %d) | Wizard ATK: %d -> %d (+10)%n",
                    enemy.getName(), initHp, enemy.getHp(),
                    preAttack, enemy.getDefense(), damage,
                    preAttack, user.getAttack()));
            }
        }

        return message.toString().stripTrailing();
    }

    @Override
    public String getName()
    {
        return "ArcaneBlast";
    }
}
