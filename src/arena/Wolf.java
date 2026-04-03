package arena;

public class Wolf extends Enemy
{
    public Wolf()
    {
        super(40, 45, 5, 35);
    }

    @Override
    public String getName()
    {
        return "Wolf";
    }
}