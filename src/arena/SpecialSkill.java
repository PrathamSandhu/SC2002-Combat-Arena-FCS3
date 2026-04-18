package arena;

public abstract class SpecialSkill implements Action {
    protected int coolDown = 3;
    protected int curCoolDown = 0;

    public int getCoolDown() {
        return coolDown;
    }

    public int getCurCoolDown() {
        return curCoolDown;
    }

    public boolean isUsable() {
        return curCoolDown == 0;
    }

    public void triggerCoolDown() {
        curCoolDown = coolDown;
    }

    public void reduceCoolDown() {
        if (curCoolDown > 0) {
            curCoolDown--;
        }
    }

    // changed name
    public void setCoolDown(int value) {
        curCoolDown = value;
    }

    // new
    public abstract String execute(Combatant user, Combatant target, BattleEngine engine, boolean ignoreCoolDown);

    @Override
    public String execute(Combatant user, Combatant target, BattleEngine engine) {
        return execute(user, target, engine, false);
    }
    @Override
    public String getName() {
        return "SpecialSkill";
    }
}
