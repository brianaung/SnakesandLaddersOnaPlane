package snakeladder.game;

import snakeladder.utility.BackgroundDrawing;

public class Ladder extends Connection
{
  public Ladder(int ladderStart, int ladderEnd)
  {
    super(ladderStart, ladderEnd);
    setImagePath(BackgroundDrawing.SPRITES_PATH + "ladder.png");
  }

}
