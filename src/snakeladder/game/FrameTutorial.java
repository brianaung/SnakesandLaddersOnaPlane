package snakeladder.game;

import snakeladder.utility.PropertiesLoader;
import snakeladder.utility.ServicesRandom;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("serial")
public class FrameTutorial extends JFrame
{
  private final String version = "1.01";
  
  public FrameTutorial(Properties properties)
  {
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setResizable(false);
    setLocation(10, 10);
    setTitle("snakeladder.game.FrameTutorial V" + version +
      ", (Design: Carlo Donzelli, Implementation: Aegidius Pluess)");
    GamePane gp = new GamePane(properties);
    getContentPane().add(gp, BorderLayout.WEST);
    NavigationPane np = new NavigationPane(properties);
    getContentPane().add(np, BorderLayout.EAST);
    np.setGamePlayCallback(new GamePlayCallback() {
      @Override
      public void finishGameWithResults(int winningPlayerIndex, List<String> playerCurrentPositions) {
       System.out.println("DO NOT CHANGE THIS LINE---WINNING INFORMATION: " + winningPlayerIndex + "-" + String.join(",", playerCurrentPositions));
      }
    });

    pack();  // Must be called before actors are added!

    np.setGamePane(gp);
    np.createGui();
    gp.setNavigationPane(np);
    gp.createGui();
    np.checkAuto();
  }

  public static void main(String[] args)
  {
    final Properties properties;
    if (args == null || args.length == 0) {
      properties = PropertiesLoader.loadPropertiesFile(null);
    } else {
      properties = PropertiesLoader.loadPropertiesFile(args[0]);
    }
    String seedProp = properties.getProperty("seed");
    Long seed = null;
    if (seedProp != null) seed = Long.parseLong(seedProp);
    ServicesRandom.initServicesRandom(seed);
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        new snakeladder.game.FrameTutorial(properties).setVisible(true);
      }

    });
  }

}
