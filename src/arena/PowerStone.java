package arena;

public class PowerStone implements Item {
    @Override
    public String use(Player user, BattleEngine engine) {
        SpecialSkill skill = user.getSpecialSkill();
        int before = skill.getCurCoolDown();
        String result = skill.execute(user, engine.selectTarget(engine.getEnemies()), engine);
        skill.curCoolDown = before;

        return String.format("%s used: %s | Cooldown unchanged -> %d (%s does not affect cooldown) | %s consumed", getName(), result,
                            before, getName(), getName());
    }

    @Override
    public String getName() {
        return "PowerStone";
    }
}
