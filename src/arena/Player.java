package arena;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public abstract class Player extends Combatant
{
    protected List<Item> items;
    protected SpecialSkill specialSkill;
    protected Set<Class<? extends Item>> originalItemTypes;

    public Player(int hp, int attack, int defense, int speed, SpecialSkill specialSkill)
    {
        super(hp, attack, defense, speed);
        this.items = new ArrayList<>();
        this.originalItemTypes = new HashSet<>();
        this.specialSkill = specialSkill;
    }

    // Note: takeTurn() is intentionally not used by BattleEngine — the engine
    // drives turns directly via processRound() for full control over turn order,
    // stun checks, and logging. This method is kept to satisfy the abstract
    // contract from Combatant but should not be called externally.
    @Override
    public void takeTurn(List<Combatant> combatants, BattleEngine engine)
    {
        processEffects();
        Action action = chooseAction(combatants, engine);
        Combatant target = engine.selectTarget(engine.getEnemies());
        action.execute(this, target, engine);
        specialSkill.reduceCoolDown();
    }

    public void addItem(Item item)
    {
        items.add(item);
        originalItemTypes.add(item.getClass());
    }
    
    public boolean hadItemType(Class<? extends Item> type) {
    	return originalItemTypes.contains(type);
    }

    public void removeItem(Item item)
    {
        items.remove(item);
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
