public class Defend implements Action {
  @Override
  public String execute(Combatant user, Combatant target, BattleEngine engine) {
    int initDefense = user.getDefense();
    user.apply(new DefendEffect());

    return String.format("%s -> %s -> Defended: Defense: (%d + 10 = %d)| %s defense increased (2 turns)" user.getName(), getName(), initDefense, user.getDefense(), user.getName());
  }

  @Override
  public String getName() {
    return "Defend";
  }
}
