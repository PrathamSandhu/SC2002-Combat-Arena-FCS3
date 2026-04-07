package arena;

public class SmokeBomb implements Item {
    @Override
    public String use(Player user, BattleEngine engine) {
        user.applyEffect(new SmokeBombEffect());
        return String.format("%s used: damage taken = 0 (2 turns)", getName());
    }

    @Override
    public String getName() {
        return "SmokeBomb";
    }
}
