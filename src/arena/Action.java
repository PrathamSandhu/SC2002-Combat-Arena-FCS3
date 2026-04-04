package arena;

public interface Action {
  String execute(Combatant user, Combatant target, BattleEngine engine);

  String getName();
}
