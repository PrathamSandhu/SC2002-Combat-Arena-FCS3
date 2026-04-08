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

    // Input helpers
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
        if (difficulty == Difficulty.EASY) {
            System.out.print("Level: Easy (" + enemies.size() +  "Goblins) - ");

            for (int i = 0; i < enemies.size(); i++) {
                char label = (char) ('A' + i);
                System.out.print(label);
                if (i < enemies.size() - 1) {
                    System.out.print(", ");
                }
            }

            Combatant e = enemies.get(0);
            System.out.printf(", %s Stats: HP: %d, ATK: %d, DEF: %d, SPD: %d",
                    e.getName(), e.getHp(), e.getAttack(), e.getDefense(), e.getSpeed());
        }
        else {
            System.out.print("Level: " + difficulty.name().charAt(0) + difficulty.name().substring(1).toLowerCase() + " - ");

            int goblinCount = 0;
            int wolfCount = 0;

            for (Combatant e : enemies) {
                if (e instanceof Goblin) {
                    goblinCount++;
                }
                else if (e instanceof Wolf) {
                    wolfCount++;
                }
            }

            boolean first = true;
            if (goblinCount > 0) {
                System.out.print(goblinCount + " Goblin");
                first = false;
            }

            if (wolfCount > 0) {
                if (!first) {
                    System.out.print(" + ");
                }
                System.out.print(wolfCount + " Wolf");
            }

            List<Combatant> backupEnemies = engine.getBackupEnemies();
            System.out.print(" | Backup: ");

            goblinCount = 0;
            List<Combatant> wolves = new ArrayList<>();

            for (Combatant e : backupEnemies) {
                if (e instanceof Goblin) {
                    goblinCount++;
                }
                else if (e instanceof Wolf) {
                    wolves.add(e);
                }
            }

            first = true;
            if (goblinCount > 0) {
                System.out.print(goblinCount + " Goblin");
                first = false;
            }

            for (int i = 0; i < wolves.size(); i++) {
                if (!first) {
                    System.out.print(" + ");
                }

                char label = (char) ('A' + i);
                System.out.print("Wolf " + label);

                first = false;
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

}
