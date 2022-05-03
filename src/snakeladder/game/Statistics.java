package snakeladder.game;

import java.util.*;

public class Statistics {
    private int traversedUp=0;
    private int traversedDown=0;
    // Tree map is a sorted Dictionary
    private TreeMap<Integer,Integer> playerRolls;

    public Statistics() {
        playerRolls = new TreeMap<>();
    }

    public void addTraversedUp() {
        traversedUp++;
    }
    public void addTraversedDown() {
        traversedDown++;
    }

    public TreeMap<Integer, Integer> getPlayerRolls() {
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
        //ArrayList<String> rolled = new ArrayList<String>();
        Set set = playerRolls.entrySet();
        Iterator it = set.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            rolledOutput += entry.getKey() + "-" + entry.getValue() + " ";
            //rolled.add(entry.getKey() + "-" + entry.getValue() + " ");
        }
//        Collections.reverse(rolled);
//        for (int i=0; i<rolled.size(); i++) {
//            rolledOutput += rolled.get(i);
//        }
        return rolledOutput;
    }

    public String toTraversedString() {
        String traversedOutput = " traversed: up-" + traversedUp + " down-" + traversedDown;
        return traversedOutput;
    }
}
