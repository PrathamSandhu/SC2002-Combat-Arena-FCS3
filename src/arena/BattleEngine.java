package arena;

import java.util.ArrayList;
import java.util.List;

public class BattleEngine
{
    private Player player;
    private List<Combatant> activeEnemies;
    private List<Combatant> backupEnemies;
    private TurnOrderStrategy turnOrderStrategy;
    private int round;
    private boolean backupSpawned;

    public BattleEngine(Player player, List<Enemy> initialEnemies, List<Enemy> backupEnemies, TurnOrderStrategy turnOrderStrategy)
    {
        this.player = player;
        this.activeEnemies = new ArrayList<>(initialEnemies);
        this.backupEnemies = new ArrayList<>(backupEnemies);
        this.turnOrderStrategy = turnOrderStrategy;
        this.round = 1;
        this.backupSpawned = false;
    }

    public List<String> processRound(Action playerAction, Combatant playerTarget)
    {
        List<String> log = new ArrayList<>();
        List<Combatant> allCombatants = getTurnOrder();

        log.add("--- Round " + round + " ---");

        for (Combatant combatant : allCombatants)
        {
            if (!combatant.isAlive())
                continue;

            if (combatant.hasEffect(Stun.class))
            {
                combatant.processEffects();
                log.add(combatant.getName() + " is STUNNED and skips their turn.");
                continue;
            }

            combatant.processEffects();

            if (combatant instanceof Player)
            {
                String result = playerAction.execute(combatant, playerTarget, this);
                log.add(result);
                // Reduce special skill cooldown after the player acts
                player.getSpecialSkill().reduceCoolDown();
            }
            else
            {
                if (player.hasEffect(SmokeBombEffect.class))
                {
                    log.add(combatant.getName() + " attacks " + player.getName() + " but Smoke Bomb blocks it!");
                }
                else
                {
                    Action basicAttack = new BasicAttack();
                    String result = basicAttack.execute(combatant, player, this);
                    log.add(result);
                }
            }

            if (isGameOver())
                break;
        }

        checkBackupSpawn(log);
        round++;
        return log;
    }

    private void checkBackupSpawn(List<String> log)
    {
        if (!backupSpawned && activeEnemies.stream().noneMatch(Combatant::isAlive))
        {
            if (!backupEnemies.isEmpty())
            {
                activeEnemies.addAll(backupEnemies);
                backupEnemies.clear();
                backupSpawned = true;
                log.add("Backup enemies have spawned!");
                for (Combatant enemy : activeEnemies)
                {
                    if (enemy.isAlive())
                        log.add(" + " + enemy.getName() + " entered the battle!");
                }
            }
        }
    }

    public List<Combatant> getTurnOrder()
    {
        List<Combatant> all = new ArrayList<>();
        all.add(player);
        all.addAll(activeEnemies);
        return turnOrderStrategy.getOrder(all);
    }

    public boolean isGameOver()
    {
        return !player.isAlive() || getAliveEnemies().isEmpty();
    }

    public boolean isPlayerDefeated()
    {
        return !player.isAlive();
    }

    public boolean isPlayerVictorious()
    {
        return player.isAlive() && getAliveEnemies().isEmpty();
    }

    // Returns only alive enemies (used for game-state checks)
    public List<Combatant> getEnemies()
    {
        return getAliveEnemies();
    }

    private List<Combatant> getAliveEnemies()
    {
        List<Combatant> alive = new ArrayList<>();
        for (Combatant c : activeEnemies)
        {
            if (c.isAlive())
                alive.add(c);
        }
        return alive;
    }

    public List<Combatant> getBackupEnemies()
    {
        return new ArrayList<>(backupEnemies);
    }

    public Player getPlayer()
    {
        return player;
    }

    public int getRound()
    {
        return round;
    }

    public String getSummary()
    {
        if (isPlayerVictorious())
        {
            return String.format(
                "Congratulations, you have defeated all your enemies!%n" +
                "Statistics: Remaining HP: %d/%d | Total Rounds: %d",
                player.getHp(), player.getMaxHp(), round - 1);
        }
        else
        {
            return String.format(
                "Defeated. Don't give up, try again!%n" +
                "Statistics: Enemies remaining: %d | Total Rounds Survived: %d",
                getAliveEnemies().size(), round - 1);
        }
    }

    // Bug fix: previously both branches returned enemies.get(0), so multi-enemy
    // targeting (e.g. via PowerStone) always hit the first enemy regardless of
    // the player's choice. Now accepts the caller's pre-selected target directly.
    public Combatant selectTarget(List<Combatant> candidates)
    {
        if (candidates == null || candidates.isEmpty())
            return null;
        return candidates.get(0);
    }
}