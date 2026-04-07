package arena;

public class PowerStone implements Item {
    @Override
    public String use(Player user, BattleEngine engine) {
        Action skill = user.getSpecialSkill();
        String result = skill.execute(user, engine.selectTarget(), engine); // see engine code first

        return String.format("%s used:%n%s", getName(), result); // commit
    }

    @Override
    public String getName() {
        return "PowerStone";
    }
}
