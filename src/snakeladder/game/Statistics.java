package snakeladder.game;

import java.util.*;

public class Statistics {
    private int traversedUp=0;
    private int traversedDown=0;
    private Hashtable<Integer,Integer> playerRolls;

    public Statistics() {
        playerRolls = new Hashtable<>();
    }

    public void addTraversedUp() {
        traversedUp++;
    }
    public void addTraversedDown() {
        traversedDown++;
    }

    public Hashtable<Integer, Integer> getPlayerRolls() {
        return playerRolls;
    }

    public int getTraversedUp() {
        return traversedUp;
    }

    public int getTraversedDown() {
        return traversedDown;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "traversedUp=" + traversedUp +
                ", traversedDown=" + traversedDown +
                ", playerRolls=" + playerRolls +
                '}';
    }
}
