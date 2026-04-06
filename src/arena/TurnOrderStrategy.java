package arena;

import java.util.List;

public interface TurnOrderStrategy
{
    List<Combatant> getOrder(List<Combatant> combatants);
}