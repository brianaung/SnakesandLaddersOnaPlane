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
  private int y;
  private int dy;
  private boolean isAuto;
  private String puppetName;

  // Added variables
  // Track the number of moves performed by each puppet at each time
  private int nMoves = 0;
  private int numberOfDice;
  // Total moves per turn
  private int turnMoves = 0;

  Puppet(GamePane gp, NavigationPane np, String puppetImage)
  {
    super(puppetImage);
    this.gamePane = gp;
    this.navigationPane = np;
    this.numberOfDice = np.getNumberOfDice();
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

  void go(int nbSteps)
  {
    if (cellIndex == 100)  // after game over
    {
      cellIndex = 0;
      setLocation(gamePane.startLocation);
    }

    // allow player to roll multiple dice (based on specified numberOfDice) 
    // before moving the puppet
    nMoves += nbSteps;
    // System.out.println("nMoves = " + nMoves);
    // System.out.println("DieIndex = " + navigationPane.getDieIndex());
    if (navigationPane.getDieIndex() == navigationPane.getNumberOfDice()) {
      this.nbSteps = nMoves;
      endTurn();
    } else {
      // Don't move puppet if player hasn't rolled the dice for specified time yet
      this.nbSteps = 0;
      navigationPane.setDieIndex(navigationPane.getDieIndex() + 1);
    }

    // need to run act() even if puppet dont need to move to prepare next roll
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
    if (tens % 2 == 0) {
      // Cells starting left 01, 21, .. 81
      if (ones == 0 && cellIndex > 0)
        setLocation(new Location(getX(), getY() + 1));
      else
        setLocation(new Location(getX() - 1, getY()));
    }
    else {
      // Cells starting left 20, 40, .. 100
      if (ones == 0)
        setLocation(new Location(getX(), getY() + 1));
      else
        setLocation(new Location(getX() + 1, getY()));
    }
    cellIndex--;
  }

  public void act()
  {
    if ((cellIndex / 10) % 2 == 0)
    {
      if (isHorzMirror())
        setHorzMirror(false);
    }
    else
    {
      if (!isHorzMirror())
        setHorzMirror(true);
    }

    // Animation: Move on connection
    if (currentCon != null)
    {
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
      return;
    }

    // Normal movement
    if (nbSteps > 0)
    {
      moveToNextCell();

      if (cellIndex == 100)  // Game over
      {
        setActEnabled(false);
        navigationPane.prepareRoll(cellIndex);
        return;
      }
      nbSteps--;

      // After finish moving, determine if puppet is on a connection
      if (nbSteps == 0)
      {

        // TODO: should implement task 3 here?
        // below code's not fully working
        // int prevPuppetIndex = gamePane.getCurrentPuppetIndex();
        // int prevPuppetCell = gamePane.getAllPuppets().get(prevPuppetIndex).getCellIndex();
        // int currPuppetCell = this.cellIndex;
        // Puppet prevPuppet = (gamePane.getAllPuppets()).get(prevPuppetIndex);
        //
        // System.out.println("curr cell index:" + currPuppetCell);
        // System.out.println("prev cell index:" + prevPuppetCell);
        // System.out.println("prev puppet index:" + prevPuppetIndex);
        //
        // if (currPuppetCell == prevPuppetCell) {
        //   prevPuppet.moveToPrevCell();
        //   return;
        // }

        // Check if on connection start
        if ((currentCon = gamePane.getConnectionAt(getLocation())) != null)
        {
          gamePane.setSimulationPeriod(50);
          y = gamePane.toPoint(currentCon.locStart).y;
          if (currentCon.locEnd.y > currentCon.locStart.y) {
            // Connection is Snake
            if (turnMoves != numberOfDice) {
              dy = gamePane.animationStep;
            } else {
              currentCon = null;
              navigationPane.prepareRoll(cellIndex);
            }
          }
          else {
            // Connection is Ladder
            dy = -gamePane.animationStep;
          }
          if (currentCon instanceof Snake) {
            navigationPane.showStatus("Digesting...");
            navigationPane.playSound(GGSound.MMM);
          } else if (currentCon == null) {
            // connection is snaked but does not digest
          } else {
            navigationPane.showStatus("Climbing...");
            navigationPane.playSound(GGSound.BOING);
          }
        }
        else
        {
          setActEnabled(false);
          navigationPane.prepareRoll(cellIndex);
        }
      }
    } else if (nbSteps == 0) {
        // prepare next roll right away if puppet is specified not to move yet
        setActEnabled(false);
        navigationPane.prepareRoll(cellIndex);
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

}
