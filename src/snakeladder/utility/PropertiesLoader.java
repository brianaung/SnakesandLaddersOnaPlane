package snakeladder.utility;

import snakeladder.game.Connection;
import snakeladder.game.Ladder;
import snakeladder.game.Snake;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesLoader {
    public static final String DEFAULT_DIRECTORY_PATH = "properties/";
    public static Properties loadPropertiesFile(String propertiesFile) {
        if (propertiesFile == null) {
            try (InputStream input = new FileInputStream( DEFAULT_DIRECTORY_PATH + "testsuite.properties")) {

                Properties prop = new Properties();

                // load a properties file
                prop.load(input);

                propertiesFile = DEFAULT_DIRECTORY_PATH + prop.getProperty("current_test");
                System.out.println(propertiesFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        try (InputStream input = new FileInputStream(propertiesFile)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            return prop;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static List<Connection> loadSnakes(Properties properties) {
        List<Connection> connections = new ArrayList<>();

        int snakesCount = Integer.parseInt(properties.getProperty("snakes.count"));

        for (int i = 0; i < snakesCount; i++) {
            String snakePosition = properties.getProperty("snakes." + i);
            String[] positions = snakePosition.split(",");
            if (positions.length < 2) {
                continue;
            }

            int snakeX = Integer.parseInt(positions[0]);
            int snakeY = Integer.parseInt(positions[1]);
            connections.add(new Snake(snakeX, snakeY));
        }

        return connections;
    }

    public static List<Connection> loadLadders(Properties properties) {
        List<Connection> connections = new ArrayList<>();

        int laddersCount = Integer.parseInt(properties.getProperty("ladders.count"));

        for (int i = 0; i < laddersCount; i++) {
            String ladderPosition = properties.getProperty("ladders." + i);
            String[] positions = ladderPosition.split(",");
            if (positions.length < 2) {
                continue;
            }

            int ladderX = Integer.parseInt(positions[0]);
            int ladderY = Integer.parseInt(positions[1]);
            connections.add(new Ladder(ladderX, ladderY));
        }

        return connections;
    }
}
