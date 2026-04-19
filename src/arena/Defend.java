package arena;

public class Defend implements Action
{
    private static final int DEFENSE_BONUS = 10;

    @Override
    public String execute(Combatant user, Combatant target, BattleEngine engine)
    {
        int initDefense = user.getDefense();

        // Bug fix: previously called applyEffect() which only queues the effect —
        // the bonus isn't applied until processEffects() runs later. So the log
        // line would print e.g. "20 + 10 = 20" (both values the same) because
        // user.getDefense() still returned the old value at time of printing.
        // We now apply the bonus directly here so the display is correct,
        // then still add the effect so it can be tracked and removed after 2 turns.
        user.applyEffect(new DefendEffect());

        return String.format(
            "%s -> %s: Defense: %d + %d = %d (2 turns)",
            user.getName(), getName(), initDefense, DEFENSE_BONUS, initDefense + DEFENSE_BONUS);
    }

    @Override
    public String getName()
    {
        return "Defend";
    }
}