package arena;

public class Wolf extends Enemy
{
	private final String name;
	
    public Wolf()
    {
        super(40, 45, 5, 35);
        this.name = "Wolf";
    }
    
    public Wolf(String name)
    {
        super(40, 45, 5, 35);
        this.name = name;
    }
    @Override
    public String getName()
    {
        return this.name;
    }
}
