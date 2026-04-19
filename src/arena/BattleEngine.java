package arena;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BattleEngine
{
    private Player player;
    private List<Combatant> activeEnemies;
    private List<Combatant> backupEnemies;
    private TurnOrderStrategy turnOrderStrategy;
    private int round;
    private boolean backupSpawned;
    private List<String> currentRoundLog;
    private int preTurnCoolDown;
    private Set<Combatant> eliminatedLoggedThisRound;
    private Set<Combatant> eliminatedThisRound;

    public BattleEngine(Player player, List<Enemy> initialEnemies, List<Enemy> backupEnemies, TurnOrderStrategy turnOrderStrategy)
    {
        this.player = player;
        this.activeEnemies = new ArrayList<>(initialEnemies);
        this.backupEnemies = new ArrayList<>(backupEnemies);
        this.turnOrderStrategy = turnOrderStrategy;
        this.round = 1;
        this.backupSpawned = false;
    }

    // Called by Enemy.performAttack() to append lines into the current round log
    public void log(String message)
    {
        if (currentRoundLog != null)
            currentRoundLog.add(message);
    }

    public List<String> processRound(Action playerAction, Combatant playerTarget)
    {
        currentRoundLog = new ArrayList<>();
        eliminatedLoggedThisRound = new HashSet<>();
        eliminatedThisRound = new HashSet<>();
        List<Combatant> allCombatants = getTurnOrder();

        currentRoundLog.add("--- Round " + round + " ---");

        for (Combatant combatant : allCombatants)
        {
            if (!combatant.isAlive()) {
                if (eliminatedThisRound.contains(combatant) && !eliminatedLoggedThisRound.contains(combatant)) {
                    currentRoundLog.add(combatant.getName() + "-> ELIMINATED: skipped");
                    eliminatedLoggedThisRound.add(combatant);
                }
                continue;
            }

            if (combatant.hasEffect(Stun.class))
            {
                combatant.processEffects();
                currentRoundLog.add(combatant.getName() + " -> STUNNED: turn skipped");
                continue;
            }

            combatant.processEffects();

            if (combatant instanceof Player)
            {
            	preTurnCoolDown = player.getSpecialSkill().getCurCoolDown();
            	String result = playerAction.execute(combatant, playerTarget, this);
                currentRoundLog.add(result);
                player.getSpecialSkill().reduceCoolDown();
            }
            else
            {
                combatant.takeTurn(allCombatants, this);
            }

            if (isGameOver())
                break;
        }

        checkBackupSpawn(currentRoundLog);
        round++;
        return currentRoundLog;
    }

    public void registerElimination(Combatant target) {
        eliminatedThisRound.add(target);
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

    public int getPreTurnCoolDown() {
        return preTurnCoolDown;
    }

    public Combatant selectTarget(List<Combatant> candidates)
    {
        if (candidates == null || candidates.isEmpty())
            return null;
        return candidates.get(0);
    }
}
