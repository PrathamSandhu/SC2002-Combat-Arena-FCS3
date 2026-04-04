public class BasicAttack implements Action {
  @Override
  public void execute(Combatant user, Combatant target, BattleEngine engine) {
    int damage = Math.max(0, user.getAttack() = target.getDefense());
    
  }
}
