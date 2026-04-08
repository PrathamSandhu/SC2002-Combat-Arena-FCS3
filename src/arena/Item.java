package arena;

public interface Item {
  String use(Player user, BattleEngine engine);

  String getName();
}
