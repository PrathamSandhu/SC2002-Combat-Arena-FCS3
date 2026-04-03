package arena;

public class Stun extends StatusEffect
{
    public Stun()
    {
        super(2);
    }

    @Override
    public void apply(Combatant combatant) {}

    @Override
    public String getName()
    {
        return "Stun";
    }
}