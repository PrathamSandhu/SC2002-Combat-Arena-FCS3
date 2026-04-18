package arena;

public class ShieldBash extends SpecialSkill {
    @Override
    public String execute(Combatant user, Combatant target, BattleEngine engine) {
        if (!isUsable()) {
            return "ShieldBash on cooldown!";
        }

        int damage = Math.max(0, user.getAttack() - target.getDefense());
        int initHp = target.getHp();
        target.takeDamage(damage);
        Stun stun = new Stun();
        target.applyEffect(stun);
        triggerCoolDown();

        if (target.getHp() > 0) {
            return String.format("%s -> %s -> %s: HP: %d -> %d (dmg: %d - %d = %d) | %s STUNNED (%d turns)", user.getName(), getName(), target.getName(), initHp, target.getHp(), user.getAttack(), target.getDefense(), damage, target.getName(), stun.getDuration());
        }
        else {
            return String.format("%s -> %s -> %s: HP: %d -> %d ELIMINATED (dmg: %d - %d = %d)", user.getName(), getName(), target.getName(), initHp, target.getHp(), user.getAttack(), target.getDefense(), damage);
        }
    }

    @Override
    public String getName() {
        return "ShieldBash";
    }
}
