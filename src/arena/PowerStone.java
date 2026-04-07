package arena;

public class PowerStone implements Item {
    @Override
    public String use(Player user, BattleEngine engine) {
        Action skill = user.getSpecialSkill();
        skill.execute(user, engine.selectTarget(), engine); // see engine code first

        return String.format("%s -> Item -> %s used: extra SpecialSkill used", user.getName(), getName());
    }

    @Override
    public String getName() {
        return "PowerStone";
    }
}
