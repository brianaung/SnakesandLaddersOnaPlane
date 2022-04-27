package snakeladder.utility;

import java.util.Random;

public class ServicesRandom {
    private static Random random;

    private ServicesRandom() {}

    // class requires explicit initialisation and assumes usage of random is serialised
    public static void initServicesRandom(Long seed) {
        if (random == null) {
            if (seed == null) {
                random = new Random();
                System.out.println("Seed = null");
            } else { // Use specified seed
                random = new Random(seed);
                System.out.println("Seed = " + seed);
            }
        }
    }

    public static Random get() {
        return random;
    }
}

