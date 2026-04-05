package arena;

public class ArcaneBlast extends SpecialSkill {
    @Override
    public String execute(Combatant user, Combatant target, BattleEngine engine) {
        if (!isUsable()) {
            return "ShieldBash on cooldown!";
        }

        StringBuilder message = new StringBuilder();

        for (Combatant enemy : engine.getEnemies()) { // see engine code first
            int damage = Math.max(0, user.getAttack() - target.getDefense());
            int initHp = enemy.getHp();
            enemy.takeDamage(damage);

            if (enemy.getHp() > 0) {
                message.append(String.format("%s: HP: %d -> %d (dmg: %d - %d = %d)%n", enemy.getName(), initHp, enemy.getHp(), user.getAttack(), enemy.getDefense(), damage));
            }
            else {
                // user +10 atk
                message.append(String.format("%s: HP: %d -> %d ELIMINATED (dmg: %d - %d = %d)%n", enemy.getName(), initHp, enemy.getHp(), user.getAttack(), enemy.getDefense(), damage));
            }
        }
        triggerCoolDown();

        return message.toString();
    }

    @Override
    public String getName() {
        return "ArcaneBlast";
    }
}
