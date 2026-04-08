package arena;

import java.util.*;

public class GameUI {
    private Scanner scanner = new Scanner(System.in);

    public void startGame() {
        System.out.println("TURN-BASED COMBAT ARENA");
        printDivider();

        Player player = choosePlayer();
        chooseItems(player);
        Difficulty difficulty = chooseDifficulty();

        BattleEngine engine = createBattleEngine(player, difficulty);

        DisplayLoadingScreen(player, difficulty, engine);

        runGame(engine);
    }

    // Helpers
    private int readInt(String prompt) {
        while (true) {
            System.out.println(prompt);

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input, please enter a number.");
                scanner.next();
                continue;
            }

            return scanner.nextInt();
        }
    }

    private int readChoice(String prompt, int min, int max) {
        int choice;

        while (true) {
            choice = readInt(prompt);

            if (choice >= min && choice <= max) {
                return choice;
            }
            System.out.println("Invalid choice, please enter a valid number.");
        }
    }

    private int readIndex(String prompt, List<?> list) {
        return readChoice(prompt, 0, list.size() - 1);
    }

    private void printDivider() {
        System.out.println("------------------------------");
    }

    private List<String> getLabeledNames(List<Combatant> enemies) {
        List<String> names = new ArrayList<>();
        int goblinCount = 0;
        int wolfCount = 0;

        for (Combatant enemy : enemies) {
            if (enemy instanceof Goblin) {
                goblinCount++;
            }
            else if (enemy instanceof Wolf) {
                wolfCount++;
            }
        }

        int goblinIndex = 0;
        int wolfIndex = 0;

        for (Combatant enemy : enemies) {
            if (enemy instanceof Goblin) {
                if (goblinCount > 1) {
                    char label = (char) ('A' + goblinIndex);
                    names.add("Goblin " + label);
                }
                else {
                    names.add("Goblin");
                }
            }
            else if (enemy instanceof Wolf) {
                if (wolfCount > 1) {
                    char label = (char) ('A' + wolfIndex);
                    names.add("Wolf " + label);
                }
                else {
                    names.add("Wolf");
                }
            }
        }
        return names;
    }
    //

    // Setup
    private Player choosePlayer() {
        System.out.println("Choose a player:");
        System.out.println("Warrior (HP: 260, Attack: 40, Defense: 20, Speed: 30)");
        System.out.println("Wizard (HP: 200, Attack: 50, Defense: 10, Speed: 20)");

        int choice = readChoice("Choice: ", 1, 2);
        return (choice == 1) ? new Warrior() : new Wizard();
    }

    private void chooseItems(Player player) {
        System.out.println("Choose 2 items");
        System.out.println("1. Potion: Heals 100 HP");
        System.out.println("2. Power Stone: Free extra skill");
        System.out.println("3. Smoke Bomb: Immune to damage (2 turns)");

        for (int i = 0; i < 2; i++) {
            int choice = readChoice("Choice: ", 1, 3);

            Item item = switch (choice) {
                case 2 -> new PowerStone();
                case 3 -> new SmokeBomb();
                default -> new Potion();
            };

            player.addItem(item);
        }
    }

    private Difficulty chooseDifficulty() {
        System.out.println("Choose difficulty: ");
        System.out.println("1. Easy (Initial Spawn: 3 Goblins)");
        System.out.println("1. Medium (Initial Spawn: 1 Goblin 1 Wolf, Backup Spawn: 2 Wolf)");
        System.out.println("3. Hard (Initial spawn: 2 Goblins, Backup Spawn: 1 Goblin 2 Wolf)");

        int choice = readChoice("Choice: ", 1, 3);

        return switch (choice) {
            case 2 -> Difficulty.MEDIUM;
            case 3 -> Difficulty.HARD;
            default -> Difficulty.EASY;
        };
    }

    private BattleEngine createBattleEngine(Player player, Difficulty difficulty) {
        List<Enemy> enemies = new ArrayList<>();
        List<Enemy> backupEnemies = new ArrayList<>();

        switch (difficulty) {
            case EASY -> {
                for (int i = 0; i < 3; i++) {
                    enemies.add(new Goblin());
                }
            }
            case MEDIUM -> {
                enemies.add(new Goblin());
                enemies.add(new Wolf());
                for (int i = 0; i < 2; i++) {
                    backupEnemies.add(new Wolf());
                }
            }
            case HARD -> {
                for (int i = 0; i < 2; i++) {
                    enemies.add(new Goblin());
                }
                backupEnemies.add(new Goblin());
                for (int i = 0; i < 2; i++) {
                    backupEnemies.add(new Wolf());
                }
            }
        }

        return new BattleEngine(player, enemies, backupEnemies, new SpeedBasedOrder());
    }

    private void DisplayLoadingScreen(Player player, Difficulty difficulty, BattleEngine engine) {
        printDivider();
        System.out.printf("Player: %s, %s Stats: HP: %d, ATK: %d, DEF: %d, SPD: %d",
                player.getName(), player.getName(), player.getHp(), player.getAttack(), player.getDefense(), player.getSpeed());

        System.out.print("Items: ");
        for (Item item : player.getItems()) {
            System.out.print(item.getName() + " ");
        }
        System.out.println();

        List<Combatant> enemies = engine.getEnemies();
        System.out.print("Level: " + difficulty.name().charAt(0) + difficulty.name().substring(1).toLowerCase() + " - ");

        List<String> names = getLabeledNames(enemies);
        int goblinCount = 0;
        int wolfCount = 0;
        boolean first = true;

        for (Combatant enemy : enemies) {
            if (enemy instanceof Goblin) {
                goblinCount++;
            }
            else if (enemy instanceof Wolf) {
                wolfCount++;
            }
        }
        if (goblinCount == 1) {
            System.out.print("1 Goblin");
            first = false;
        }
        else if (goblinCount > 1) {
            for (String name : names) {
                if (name.contains("Goblin")) {
                    if (!first) {
                        System.out.print(" + ");
                    }
                    System.out.print(name);
                    first = false;
                }
            }
        }

        if (wolfCount == 1) {
            if (!first) {
                System.out.print(" + ");
            }
            System.out.print("1 Wolf");
        }
        else if (wolfCount > 1) {
            for (String name : names) {
                if (name.contains("Wolf")) {
                    if (!first) {
                        System.out.print(" + ");
                    }
                    System.out.print(name);
                    first = false;
                }
            }
        }

        List<Combatant> backupEnemies = engine.getBackupEnemies();
        if (!backupEnemies.isEmpty()) {
            System.out.print(" | Backup: ");
            List<String> backupNames = getLabeledNames(backupEnemies);
            goblinCount = 0;
            wolfCount = 0;
            first = true;

            for (Combatant enemy : backupEnemies) {
                if (enemy instanceof Goblin) {
                    goblinCount++;
                }
                else if (enemy instanceof Wolf) {
                    wolfCount++;
                }
            }
            if (goblinCount == 1) {
                System.out.print("1 Goblin");
                first = false;
            }
            else if (goblinCount > 1) {
                for (String name : backupNames) {
                    if (name.contains("Goblin")) {
                        if (!first) {
                            System.out.print(" + ");
                        }
                        System.out.print(name);
                        first = false;
                    }
                }
            }

            if (wolfCount == 1) {
                if (!first) {
                    System.out.print(" + ");
                }
                System.out.print("1 Wolf");
            }
            else if (wolfCount > 1) {
                for (String name : backupNames) {
                    if (name.contains("Wolf")) {
                        if (!first) {
                            System.out.print(" + ");
                        }
                        System.out.print(name);
                        first = false;
                    }
                }
            }
        }
        System.out.println();

        List<Combatant> order = engine.getTurnOrder();
        System.out.print("Turn Order: ");
        for (int i = 0; i < order.size(); i++) {
            Combatant combatant = order.get(i);
            System.out.print(combatant.getName() + " (SPD " + combatant.getSpeed() + ")");
            if (i < order.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }
    //

    // Gameplay
    private void runGame(BattleEngine engine) {
        while (!engine.isGameOver()) {
            System.out.println("Round " + engine.getRound());

            Action action = chooseAction(engine);
            Combatant target = chooseTarget(engine, action);

            List<Combatant> enemies = engine.getEnemies();
            List<String> names = getLabeledNames(enemies);

            List<String> logs = engine.processRound(action, target);

            for (String log : logs) {
                for (int i = 0; i < enemies.size(); i++) {
                    log = log.replace(enemies.get(i).getName(), names.get(i)); // probably need a better way of doing this
                }
                System.out.println(log);
            }

            displayEndOfRound(engine);
            printDivider();
        }
        displayFinalResult(engine);
    }
    //

    // Choose actions
    private Action chooseAction(BattleEngine engine) {
        Player player = engine.getPlayer();

        System.out.println("Choose your action: ");
        System.out.println("1. Basic Attack");
        System.out.println("2. Defend");
        System.out.println("3. Use Item");
        System.out.println("4. Special Skill");

        int max = player.hasItems() ? 4 : 3;
        int choice = readChoice("Choice: ", 1, max);

        return switch (choice) {
            case 2 -> new Defend();
            case 3 -> chooseItem(player);
            case 4 -> player.getSpecialSkill();
            default -> new BasicAttack();
        };
    }

    private Action chooseItem(Player player) {
        List<Item> items = player.getItems();

        for (int i = 0; i < items.size(); i++) {
            System.out.printf("%d. %s%n", i, items.get(i).getName());
        }

        int index = readIndex("Choice: ", items);
        return new UseItem(items.get(index));
    }

    private Combatant chooseTarget(BattleEngine engine, Action action) {
        if (action instanceof Defend || action instanceof UseItem) {
            return null;
        }

        List<Combatant> enemies = engine.getEnemies();
        List<String> names = getLabeledNames(enemies);

        System.out.println("Choose your target:");

        for (int i = 0; i < enemies.size(); i++) {
            System.out.printf("%d. %s (HP: %d%n", i, names.get(i), enemies.get(i).getHp());
        }

        int index = readIndex("Choice: ", enemies);
        return enemies.get(index);
    }
    //

    // Display
    private void displayEndOfRound(BattleEngine engine) {
        Player player = engine.getPlayer();
        List<Combatant> enemies = engine.getEnemies();
        List<String> names = getLabeledNames(enemies);

        System.out.printf("End of Round %d %s HP: %d/%d | ",
                engine.getRound(), player.getName(), player.getHp(), player.getMaxHp());

        for (int i = 0; i < enemies.size(); i++) {
            System.out.printf("%s HP: %d | ", names.get(i), enemies.get(i).getHp());
        }

        int potion = 0;
        int powerStone = 0;
        int smokeBomb = 0;

        for (Item item : player.getItems()) {
            if (item instanceof Potion) {
                potion++;
            }
            if (item instanceof PowerStone) {
                powerStone++;
            }
            if (item instanceof SmokeBomb) {
                smokeBomb++;
            }
        }

        if (potion > 0) {
            System.out.printf("Potion: %d | ", potion);
        }
        if (powerStone > 0) {
            System.out.printf("Power Stone: %d | ", powerStone);
        }
        if (smokeBomb > 0) {
            System.out.printf("Smoke Bomb: %d | ", smokeBomb);
        }

        System.out.printf("Special Skills Cooldown: %d rounds%n", player.getSpecialSkill().getCurCoolDown());
    }

    private void displayFinalResult(BattleEngine engine) {
        System.out.println(engine.getSummary());
    }
}
