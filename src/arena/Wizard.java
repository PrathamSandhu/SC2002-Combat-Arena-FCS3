package arena;

import java.util.List;

public class Wizard extends Player
{
    public Wizard()
    {
        super(200, 50, 10, 20, new ArcaneBlast());
    }

    @Override
    public Action chooseAction(List<Combatant> combatants, BattleEngine engine)
    {
        return new BasicAttack();
    }

    @Override
    public String getName()
    {
        return "Wizard";
    }
}