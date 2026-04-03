package arena;

public abstract class StatusEffect
{
    protected int duration;

    public StatusEffect(int duration)
    {
        this.duration = duration;
    }

    public abstract void apply(Combatant combatant);

    public void tick()
    {
        if (duration > 0)
            duration--;
    }

    public boolean isExpired()
    {
        return duration <= 0;
    }

    public int getDuration()
    {
        return duration;
    }

    public abstract String getName();
}