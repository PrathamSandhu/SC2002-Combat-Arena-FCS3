package arena;

import java.util.ArrayList;
import java.util.List;

public abstract class Player extends Combatant
{
    protected List<Item> items;
    protected SpecialSkill specialSkill;

    public Player(int hp, int attack, int defense, int speed, SpecialSkill specialSkill)
    {
        super(hp, attack, defense, speed);
        this.items = new ArrayList<>();
        this.specialSkill = specialSkill;
    }

    @Override
    public void takeTurn(List<Combatant> combatants, BattleEngine engine)
    {
        processEffects();
        Action action = chooseAction(combatants, engine);
        Combatant target = engine.selectTarget(combatants);
        action.execute(this, target, engine);
        specialSkill.reduceCoolDown();
    }

    public void addItem(Item item)
    {
        items.add(item);
    }

    public boolean hasItems()
    {
        return !items.isEmpty();
    }

    public List<Item> getItems()
    {
        return items;
    }

    public SpecialSkill getSpecialSkill()
    {
        return specialSkill;
    }

    public abstract Action chooseAction(List<Combatant> combatants, BattleEngine engine);
    public abstract String getName();
}