public class BasicAttack implements Action {
  @Override
  public String execute(Combatant user, Combatant target, BattleEngine engine) {
    int damage = Math.max(0, user.getAttack() - target.getDefense());
    int initHp = target.getHp();
    target.takeDamage(damage);

    return String.format("%s -> %s -> %s: HP: %d -> %d (dmg: %d - %d = %d)", user.getName(), getName(), target.getName(), initHp, target.getHp(), user.getAttack(), target.getDefense(), damage);
  }

  @Override
  public String getName() {
      return "BasicAttack";
  }
}
