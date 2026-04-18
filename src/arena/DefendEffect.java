package arena;

public class DefendEffect extends StatusEffect
{
    private static final int DEFENSE_BONUS = 10;
    private boolean applied = false;

    public DefendEffect()
    {
        super(2);
    }

    @Override
    public void apply(Combatant combatant)
    {
        if (!applied)
        {
            combatant.defense += DEFENSE_BONUS;
            applied = true;
        }
    }

    @Override
    public void tick()
    {
        duration--;
    }

    @Override
    public boolean isExpired()
    {
        if (duration <= 0)
        {
            return true;
        }
        return false;
    }

    @Override
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
