package arena;

public class UseItem implements Action {
    private Item item;

    public UseItem(Item item) {
        this.item = item;
    }
    @Override
    public String execute(Combatant user, Combatant target, BattleEngine engine) {
        Player player = (Player) user;

        String result = item.use(player, engine);

        player.getItems().remove(item); // maybe create removeItem method in player

        return String.format("%s -> Item -> %s", player.getName(), result);
    }

    @Override
    public String getName() {
        return "UseItem: " + item.getName();
    }
}
