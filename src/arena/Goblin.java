package arena;

public class Goblin extends Enemy
{
	private final String name;
	
    public Goblin()
    {
        super(55, 35, 15, 25);
        this.name = "Goblin";
    }
    
    public Goblin(String name)
    {
        super(55, 35, 15, 25);
        this.name = name;
    }

    @Override
    public String getName()
    {
        return "Goblin";
    }
}
