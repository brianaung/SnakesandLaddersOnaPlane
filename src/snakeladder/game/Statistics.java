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

    public String toRolledString() {
        String rolledOutput = " rolled: ";
        ArrayList<String> rolled = new ArrayList<String>();
        Set set = playerRolls.entrySet();
        Iterator it = set.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            rolled.add(entry.getKey() + "-" + entry.getValue() + " ");
        }
        Collections.reverse(rolled);
        for (int i=0; i<rolled.size(); i++) {
            rolledOutput += rolled.get(i);
        }
        return rolledOutput;
    }

    public String toTraversedString() {
        String traversedOutput = " traversed: up-" + traversedUp + " down-" + traversedDown;
        return traversedOutput;
    }
}
