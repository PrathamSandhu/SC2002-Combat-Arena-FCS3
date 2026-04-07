package arena;

public class SmokeBomb implements Item {
    @Override
    public String use(Player user, BattleEngine engine) {
        SmokeBombEffect smokeBombEffect = new SmokeBombEffect();
        smokeBombEffect.apply(user);
        user.applyEffect(smokeBombEffect);
        return String.format("%s -> Item -> %s used: damage taken = 0 (2 turns)", user.getName(), getName());
    }

    @Override
    public String getName() {
        return "SmokeBomb";
    }
}
