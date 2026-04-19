package arena;

public class PowerStone implements Item {
    @Override
    public String use(Player user, BattleEngine engine) {
        // new
        SpecialSkill skill = user.getSpecialSkill();
        int preTurnCoolDown = engine.getPreTurnCoolDown();
        Combatant target= engine.selectTarget(engine.getEnemies());

        String result = skill.execute(user, target, engine, true); // see engine code first
        skill.setCoolDown(preTurnCoolDown + 1);
        
        return String.format("%s used -> %s | Cooldown unchanged -> %d (Power Stone does not affect cooldown) | Power Stone consumed",
        		getName(), result, preTurnCoolDown);
    }

    @Override
    public String getName() {
        return "PowerStone";
    }
}
