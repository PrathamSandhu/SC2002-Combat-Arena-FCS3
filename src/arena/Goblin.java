package com.arena;

public class Goblin extends Enemy
{
    public Goblin()
    {
        super(55, 35, 15, 25);
    }

    @Override
    public String getName()
    {
        return "Goblin";
    }
}