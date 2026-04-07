package arena;

public class Potion implements Item {
    @Override
    public String use(Player user, BattleEngine engine) {
        int heal = 100;
        int initHp = user.getHp();
        user.heal(heal);

        return String.format("%s -> Item -> %s used: HP: %d -> %d (+100)", user.getName(), getName(), initHp, user.getHp());
    }

    @Override
    public String getName() {
        return "Potion";
    }
}
