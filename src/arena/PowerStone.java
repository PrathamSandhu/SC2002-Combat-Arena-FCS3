package arena;

public class PowerStone implements Item {
    @Override
    public String use(Player user, BattleEngine engine) {
        SpecialSkill skill = user.getSpecialSkill();
        int before = skill.getCurCoolDown();
        String result = skill.execute(user, engine.selectTarget(engine.getEnemies()), engine); // see engine code first
        skill.curCoolDown = before;

        return String.format("%s used:%n%s", getName(), result); // commit
    }

    @Override
    public String getName() {
        return "PowerStone";
    }
}
