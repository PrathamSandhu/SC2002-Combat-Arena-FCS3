package arena;

import java.util.List;

public class Warrior extends Player
{
    public Warrior()
    {
        super(260, 40, 20, 30, new ShieldBash());
    }

    @Override
    public Action chooseAction(List<Combatant> combatants, BattleEngine engine)
    {
        return new BasicAttack();
    }

    @Override
    public String getName()
    {
        return "Warrior";
    }
}