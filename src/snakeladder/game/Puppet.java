package snakeladder.game;

import ch.aplu.jgamegrid.*;
import java.awt.Point;

public class Puppet extends Actor
{
  private GamePane gamePane;
  private NavigationPane navigationPane;
  private int cellIndex = 0;
  private int nbSteps;
  private Connection currentCon = null;
  private boolean isAuto;
  private String puppetName;
  // used to animate movement on connections
  private int y;
  private int dy;
  // Track the number of moves performed by each puppet at each time
  private int nMoves = 0;
  private int numberOfDice;
  // Total moves per turn
  private int turnMoves = 0;
  private Statistics stats;

  Puppet(GamePane gp, NavigationPane np, String puppetImage)
  {
    super(puppetImage);
    this.gamePane = gp;
    this.navigationPane = np;
    this.numberOfDice = np.getNumberOfDice();
    this.stats = new Statistics();
  }

  public boolean isAuto() {
    return isAuto;
  }

  public void setAuto(boolean auto) {
    isAuto = auto;
  }

  public String getPuppetName() {
    return puppetName;
  }

  public void setPuppetName(String puppetName) {
    this.puppetName = puppetName;
  }

  // calculate the total number of steps a puppet should move
  void go(int nb) {
    if (cellIndex == 100)  // after game over
    {
      cellIndex = 0;
      setLocation(gamePane.startLocation);
    }

    // allow player to roll multiple dice (based on specified numberOfDice) 
    // before moving the puppet
    nMoves += nb;
    if (navigationPane.getDieIndex() == navigationPane.getNumberOfDice()) {
      nbSteps = nMoves;
      stats.getPlayerRolls().putIfAbsent(nMoves, 0);
      stats.getPlayerRolls().put(nMoves, stats.getPlayerRolls().get(nMoves) + 1);
      endTurn();
      setActEnabled(true);
    } else {
      // Don't move puppet if player hasn't rolled the dice for specified time yet
      // Prepare to do next roll right away (do not `act()`)
      navigationPane.setDieIndex(navigationPane.getDieIndex() + 1);
      navigationPane.prepareRoll(cellIndex);
      setActEnabled(false);
    }
    setActEnabled(true);
  }

  void resetToStartingPoint() {
    cellIndex = 0;
    setLocation(gamePane.startLocation);
    setActEnabled(true);
  }

  int getCellIndex() {
    return cellIndex;
  }

  public Statistics getStats() {
    return stats;
  }

  private void moveToNextCell()
  {
    int tens = cellIndex / 10;
    int ones = cellIndex - tens * 10;
    if (tens % 2 == 0) {
      // Cells starting left 01, 21, .. 81
      if (ones == 0 && cellIndex > 0)
        setLocation(new Location(getX(), getY() - 1));
      else
        setLocation(new Location(getX() + 1, getY()));
    }
    else {
      // Cells starting left 20, 40, .. 100
      if (ones == 0)
        setLocation(new Location(getX(), getY() - 1));
      else
        setLocation(new Location(getX() - 1, getY()));
    }
    cellIndex++;
  }

  private void moveToPrevCell()
  {
    int tens = cellIndex / 10;
    int ones = cellIndex - tens * 10;

    if ((cellIndex % 10 == 1) && (cellIndex != 1)) {
      // for cells ending with '1' go down one cell
      setLocation(new Location(getX(), getY() + 1));
    } else if (tens % 2 == 0) {
      // row goes left to right
      if (ones == 0) {
        // last cell in row
        setLocation(new Location(getX() + 1, getY()));
      } else {
        setLocation(new Location(getX() - 1, getY()));
      }
    } else if (tens % 2 != 0) {
      // row goes right to left
      if (ones == 0) {
        // last cell in row
        setLocation(new Location(getX() - 1, getY()));
      } else {
        setLocation(new Location(getX() + 1, getY()));
      }
    }

    cellIndex--;
  }

  public void act()
  {
    if ((cellIndex / 10) % 2 == 0) {
      if (isHorzMirror()) { setHorzMirror(false); }
    } else {
      if (!isHorzMirror()) { setHorzMirror(true); }
    }
    
    // Animation: Move on connection
    if (currentCon != null) {
      animateOnConnection();
      return;
    }

    // Normal movement
    if (nbSteps > 0) {
      moveToNextCell();

      // Game over case
      if (cellIndex == 100)  {
        setActEnabled(false);
        navigationPane.prepareRoll(cellIndex);
        return;
      }

      nbSteps--;

      // After finish moving, determine if puppet is on a connection
      if (nbSteps == 0) {

        // check if puppet fall on the same cell as opponent
        int prevPuppetIndex = gamePane.getCurrentPuppetIndex();
        int prevPuppetCell = gamePane.getAllPuppets().get(prevPuppetIndex).getCellIndex();
        int currPuppetCell = this.cellIndex;
        Puppet prevPuppet = (gamePane.getAllPuppets()).get(prevPuppetIndex);
        if (currPuppetCell == prevPuppetCell) {
          prevPuppet.moveToPrevCell(); // move opponent puppet one cell back

          // if opponent fall on to the cell that has connection
          if ((prevPuppet.currentCon = gamePane.getConnectionAt(prevPuppet.getLocation())) != null) {
            prevPuppet.prepareAtConnection();
            // animate movement till the connection end
            while (prevPuppet.currentCon != null) {
              prevPuppet.animateOnConnection();
            }
          }
        }

        // Check if on connection start
        if ((currentCon = gamePane.getConnectionAt(getLocation())) != null) {
          prepareAtConnection();
        } else {
          setActEnabled(false);
          navigationPane.prepareRoll(cellIndex);
        }
      }
    }
  }

  // Used to reset all turn variables
  private void endTurn() {
    gamePane.switchToNextPuppet();
    // Reset counters
    turnMoves = nMoves;
    nMoves = 0;
    navigationPane.setDieIndex(1);
  }

  // check the connection and prepare attributes for
  // animating movement on connection
  private void prepareAtConnection() {
    gamePane.setSimulationPeriod(50);
    y = gamePane.toPoint(currentCon.locStart).y;

    if (currentCon.locEnd.y > currentCon.locStart.y) {
      // Connection is Snake
      if (turnMoves != numberOfDice) {
        dy = gamePane.animationStep;
        stats.addTraversedDown();
      } else {
        // do not digest if total moves == number of dice rolled
        currentCon = null;
        navigationPane.prepareRoll(cellIndex);
      }
    } else {
      // Connection is Ladder
      dy = -gamePane.animationStep;
      stats.addTraversedUp();
    }

    if (currentCon instanceof Snake) {
      navigationPane.showStatus("Digesting...");
      navigationPane.playSound(GGSound.MMM);
    } else if (currentCon instanceof Ladder) {
      navigationPane.showStatus("Climbing...");
      navigationPane.playSound(GGSound.BOING);
    }
  }

  // move the puppet alongside connection (if not null) one step
  private void animateOnConnection() {
    int x = gamePane.x(y, currentCon);
    setPixelLocation(new Point(x, y));
    y += dy;

    // Check end of connection
    if ((dy > 0 && (y - gamePane.toPoint(currentCon.locEnd).y) > 0)
      || (dy < 0 && (y - gamePane.toPoint(currentCon.locEnd).y) < 0))
    {
      gamePane.setSimulationPeriod(100);
      setActEnabled(false);
      setLocation(currentCon.locEnd);
      cellIndex = currentCon.cellEnd;
      setLocationOffset(new Point(0, 0));
      currentCon = null;
      navigationPane.prepareRoll(cellIndex);
    }
  }
}
