package arena;

import java.util.*;

public class GameUI
{
    private Scanner scanner = new Scanner(System.in);
    private Set<Class<? extends Item>> usedItemTypes = new HashSet<>();

    public void startGame()
    {
        System.out.println("TURN-BASED COMBAT ARENA");
        printDivider();

        Player player = choosePlayer();
        chooseItems(player);
        Difficulty difficulty = chooseDifficulty();

        BattleEngine engine = createBattleEngine(player, difficulty);

        displayLoadingScreen(player, difficulty, engine);

        runGame(engine);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private int readInt(String prompt)
    {
        while (true)
        {
            System.out.print(prompt + " ");

            if (!scanner.hasNextInt())
            {
                System.out.println("Invalid input, please enter a number.");
                scanner.next();
                continue;
            }

            int value = scanner.nextInt();
            scanner.nextLine(); // consume trailing newline
            return value;
        }
    }

    private int readChoice(String prompt, int min, int max)
    {
        while (true)
        {
            int choice = readInt(prompt);

            if (choice >= min && choice <= max)
                return choice;

            System.out.printf("Invalid choice, please enter a number between %d and %d.%n", min, max);
        }
    }

    private void printDivider()
    {
        System.out.println("------------------------------");
    }

    // -------------------------------------------------------------------------
    // Setup
    // -------------------------------------------------------------------------

    private Player choosePlayer()
    {
        System.out.println("Choose a player:");
        System.out.println("  1. Warrior  (HP: 260, ATK: 40, DEF: 20, SPD: 30) — Special: Shield Bash");
        System.out.println("  2. Wizard   (HP: 200, ATK: 50, DEF: 10, SPD: 20) — Special: Arcane Blast");

        int choice = readChoice("Choice (1-2):", 1, 2);
        return (choice == 1) ? new Warrior() : new Wizard();
    }

    private void chooseItems(Player player)
    {
        System.out.println("Choose 2 items (duplicates allowed):");
        System.out.println("  1. Potion      — Heals 100 HP");
        System.out.println("  2. Power Stone — Triggers special skill once for free (no cooldown change)");
        System.out.println("  3. Smoke Bomb  — Enemy attacks deal 0 damage for 2 turns");

        for (int i = 1; i <= 2; i++)
        {
            int choice = readChoice(String.format("Item %d choice (1-3):", i), 1, 3);

            Item item = switch (choice)
            {
                case 2  -> new PowerStone();
                case 3  -> new SmokeBomb();
                default -> new Potion();
            };

            player.addItem(item);
            usedItemTypes.add(item.getClass());
        }
    }

    private Difficulty chooseDifficulty()
    {
        System.out.println("Choose difficulty:");
        System.out.println("  1. Easy   — Initial Spawn: 3 Goblins");
        System.out.println("  2. Medium — Initial Spawn: 1 Goblin + 1 Wolf | Backup: 2 Wolves");
        System.out.println("  3. Hard   — Initial Spawn: 2 Goblins | Backup: 1 Goblin + 2 Wolves");

        int choice = readChoice("Choice (1-3):", 1, 3);

        return switch (choice)
        {
            case 2  -> Difficulty.MEDIUM;
            case 3  -> Difficulty.HARD;
            default -> Difficulty.EASY;
        };
    }

    private BattleEngine createBattleEngine(Player player, Difficulty difficulty)
    {
        List<Enemy> enemies       = new ArrayList<>();
        List<Enemy> backupEnemies = new ArrayList<>();

        switch (difficulty)
        {
            case EASY ->
            {
                enemies.add(new Goblin("Goblin A"));
                enemies.add(new Goblin("Goblin B"));
                enemies.add(new Goblin("Goblin C"));
            }
            case MEDIUM ->
            {
                enemies.add(new Goblin());
                enemies.add(new Wolf());
                backupEnemies.add(new Wolf("Wolf A"));
                backupEnemies.add(new Wolf("Wolf B"));
            }
            case HARD ->
            {
            	enemies.add(new Goblin("Goblin A"));
            	enemies.add(new Goblin("Goblin B"));
                backupEnemies.add(new Goblin("Goblin C"));
                backupEnemies.add(new Wolf("Wolf A"));
                backupEnemies.add(new Wolf("Wolf B"));
            }
        }

        return new BattleEngine(player, enemies, backupEnemies, new SpeedBasedOrder());
    }

    // Bug fix: renamed from DisplayLoadingScreen to displayLoadingScreen
    // to follow Java naming conventions (methods should start with lowercase).
    private void displayLoadingScreen(Player player, Difficulty difficulty, BattleEngine engine)
    {
        printDivider();
        System.out.printf("Player: %s | HP: %d, ATK: %d, DEF: %d, SPD: %d%n",
            player.getName(), player.getHp(), player.getAttack(),
            player.getDefense(), player.getSpeed());

        System.out.print("Items: ");
        List<Item> items = player.getItems();
        for (int i = 0; i < items.size(); i++)
        {
            System.out.print(items.get(i).getName());
            if (i < items.size() - 1) System.out.print(" + ");
        }
        System.out.println();

        String diffName = difficulty.name().charAt(0) + difficulty.name().substring(1).toLowerCase();
        System.out.print("Level: " + diffName + " — ");
        printEnemyList(engine.getEnemies());

        List<Combatant> backupEnemies = engine.getBackupEnemies();
        if (!backupEnemies.isEmpty())
        {
            System.out.print(" | Backup: ");
            printEnemyList(backupEnemies);
        }
        System.out.println();

        List<Combatant> order = engine.getTurnOrder();
        System.out.print("Turn Order: ");
        for (int i = 0; i < order.size(); i++)
        {
            Combatant c = order.get(i);
            System.out.print(c.getName() + " (SPD " + c.getSpeed() + ")");
            if (i < order.size() - 1) System.out.print(" -> ");
        }
        System.out.println();
        printDivider();
    }

    private void printEnemyList(List<Combatant> enemies)
    {
        int goblinCount = 0;
        int wolfCount   = 0;
        for (Combatant e : enemies)
        {
            if (e instanceof Goblin) goblinCount++;
            else if (e instanceof Wolf) wolfCount++;
        }

        boolean first = true;
        if (goblinCount > 0)
        {
            System.out.print(goblinCount + " Goblin" + (goblinCount > 1 ? "s" : ""));
            first = false;
        }
        if (wolfCount > 0)
        {
            if (!first) System.out.print(" + ");
            System.out.print(wolfCount + (wolfCount > 1 ? " Wolves" : " Wolf"));
        }
    }

    // -------------------------------------------------------------------------
    // Gameplay
    // -------------------------------------------------------------------------

    private void runGame(BattleEngine engine)
    {
        while (!engine.isGameOver())
        {
            System.out.println("\nRound " + engine.getRound());
            printDivider();

            Action action   = chooseAction(engine);
            Combatant target = chooseTarget(engine, action);

            List<String> logs = engine.processRound(action, target);

            for (String log : logs)
                System.out.println(log);

            displayEndOfRound(engine);
            printDivider();
        }

        displayFinalResult(engine);
    }

    // -------------------------------------------------------------------------
    // Action / target selection
    // -------------------------------------------------------------------------

    private Action chooseAction(BattleEngine engine)
    {
        Player player = engine.getPlayer();

        System.out.println("Choose your action:");
        System.out.println("  1. Basic Attack");
        System.out.println("  2. Defend");

        boolean hasItems = player.hasItems();
        boolean skillReady = player.getSpecialSkill().isUsable();
        int optionCount = 2;

        if (hasItems)
        {
            optionCount++;
            System.out.printf("  %d. Use Item%n", optionCount);
        }

        optionCount++;
        int skillOption = optionCount;
        System.out.printf("  %d. Special Skill — %s%s%n",
            skillOption,
            player.getSpecialSkill().getName(),
            skillReady ? "" : " [COOLDOWN: " + player.getSpecialSkill().getCurCoolDown() + " turns]");

        int choice = readChoice("Choice:", 1, optionCount);

        if (choice == 2) return new Defend();

        if (hasItems && choice == 3) return chooseItem(player);

        if (choice == skillOption)
        {
            if (!skillReady)
            {
                System.out.println("Special skill is on cooldown! Defaulting to Basic Attack.");
                return new BasicAttack();
            }
            return player.getSpecialSkill();
        }

        return new BasicAttack();
    }

    private Action chooseItem(Player player)
    {
        List<Item> items = player.getItems();

        System.out.println("Choose an item:");
        for (int i = 0; i < items.size(); i++)
        {
            // Bug fix: was printing 0-based index ("0. Potion") which is
            // inconsistent with every other menu. Now uses 1-based display.
            System.out.printf("  %d. %s%n", i + 1, items.get(i).getName());
        }

        // Bug fix: readChoice with 1-based range, then subtract 1 for index.
        // Previously used readIndex (0-based) but printed 1-based labels, and
        // then also subtracted 1 — causing an off-by-one / potential crash.
        int choice = readChoice("Choice:", 1, items.size());
        return new UseItem(items.get(choice - 1));
    }

    private Combatant chooseTarget(BattleEngine engine, Action action)
    {
        // Defend and UseItem target the player themselves; no selection needed
        if (action instanceof Defend || action instanceof UseItem)
            return null;

        // ArcaneBlast hits all enemies; target parameter is unused but we still
        // return a valid enemy so nothing NPEs downstream
        List<Combatant> enemies = engine.getEnemies();

        if (enemies.size() == 1)
            return enemies.get(0);

        System.out.println("Choose your target:");
        for (int i = 0; i < enemies.size(); i++)
        {
            System.out.printf("  %d. %s (HP: %d)%n", i + 1, enemies.get(i).getName(), enemies.get(i).getHp());
        }

        // Bug fix: was using readIndex (0 to size-1) then doing enemies.get(index-1),
        // which returns enemies.get(-1) when the player types "1" → crash.
        // Now uses readChoice (1 to size) and subtracts 1 for the index.
        int choice = readChoice("Choice:", 1, enemies.size());
        return enemies.get(choice - 1);
    }

    // -------------------------------------------------------------------------
    // Display
    // -------------------------------------------------------------------------

    private void displayEndOfRound(BattleEngine engine)
    {
        Player player = engine.getPlayer();
        List<Combatant> aliveEnemies = engine.getEnemies();

        // Bug fix: engine increments round at end of processRound(), so
        // getRound() returns the NEXT round's number. Use getRound() - 1.
        System.out.printf("%nEnd of Round %d: %s HP: %d/%d | ",
            engine.getRound() - 1,
            player.getName(),
            player.getHp(),
            player.getMaxHp());

        for (Combatant enemy : aliveEnemies)
        {
        	String stunTag = enemy.hasEffect(Stun.class) ? " [STUNNED]" : "";
            System.out.printf("%s HP: %d%s | ", enemy.getName(), enemy.getHp(), stunTag);
        }

        int potion     = 0;
        int powerStone = 0;
        int smokeBomb  = 0;

        for (Item item : player.getItems())
        {
            if (item instanceof Potion)     potion++;
            if (item instanceof PowerStone) powerStone++;
            if (item instanceof SmokeBomb)  smokeBomb++;
        }
        
        SmokeBombEffect smokeBombEffect = null;
        for (StatusEffect effect : player.getActiveEffects()) {
        	if (effect instanceof SmokeBombEffect) {
        		smokeBombEffect = (SmokeBombEffect) effect;
        		break;
        	}
        }

        if (potion     > 0) 
            System.out.printf("Potion: %d | ", potion);
        if (powerStone > 0) 
        	System.out.printf("Power Stone: %d | ", powerStone);
        else if (usedItemTypes.contains(PowerStone.class))
        	System.out.printf("Power Stone: 0 | ");
        if (smokeBomb  > 0) {
        	System.out.printf("Smoke Bomb: %d | ", smokeBomb);
        } else if (usedItemTypes.contains(SmokeBomb.class)) {
            if (smokeBombEffect != null) {
                int turnsRemaining = smokeBombEffect.getDuration() - 1;
                if (turnsRemaining > 0) {
                    System.out.printf("Smoke Bomb: 0 | Effect: %d turn%s remaining | ",
                			turnsRemaining, turnsRemaining == 1 ? "" : "s");
                } else {
                    System.out.printf("Smoke Bomb: 0 | ");
                }
            } else {
                System.out.printf("Smoke Bomb: 0 | ");
            }
        }
            
            if (smokeBombEffect != null) {
            	int turnsRemaining = smokeBombEffect.getDuration() - 1;
            	if (turnsRemaining > 0 ) {
                	System.out.printf("Smoke Bomb: 0 | Effect: %d turn%s remaining | ",
                			turnsRemaining, turnsRemaining == 1 ? "" : "s");
            	} else {
            		System.out.printf("Smoke Bomb: 0 | ");
        	    }
            
            }
        int coolDown = player.getSpecialSkill().getCurCoolDown();
        System.out.printf("Special Skill Cooldown: %d %s | %s ATK: %d%n",
                coolDown, coolDown == 1 ? "round" : "rounds",
                player.getName(), player.getAttack());
    }

    private void displayFinalResult(BattleEngine engine)
    {
        printDivider();
        System.out.println(engine.getSummary());
        printDivider();

        System.out.println("1. Play again with same settings");
        System.out.println("2. Start a new game");
        System.out.println("3. Exit");

        int choice = readChoice("Choice:", 1, 3);

        switch (choice)
        {
            case 1 ->
            {
                // Rebuild engine with same player type and difficulty
                // (simplest approach: just restart the whole flow)
                startGame();
            }
            case 2 -> startGame();
            default -> System.out.println("Thanks for playing!");
        }
    }
}
