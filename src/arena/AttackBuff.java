package arena;

public class AttackBuff extends StatusEffect
{
    private static final int ATTACK_BONUS = 10;
    private boolean applied = false;

    public AttackBuff()
    {
        super(Integer.MAX_VALUE);
    }

    @Override
    public void apply(Combatant combatant)
    {
        // Bug fix: guard flag prevents +10 from stacking every turn.
        // Previously: checked (duration == Integer.MAX_VALUE) which was always
        // true, so attack grew by 10 on every processEffects() call.
        if (!applied)
        {
            combatant.attack += ATTACK_BONUS;
            applied = true;
        }
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