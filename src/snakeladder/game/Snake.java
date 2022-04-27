package snakeladder.game;

import snakeladder.utility.BackgroundDrawing;
import snakeladder.utility.ServicesRandom;

public class Snake extends Connection
{
   public Snake(int snakeStart, int snakeEnd)
   {
     super(snakeStart, snakeEnd);
     int randomNum = ServicesRandom.get().nextInt(2);
     String [] snakeImages = new String[] { "snake_1.png", "snake_2.png" };
     setImagePath(BackgroundDrawing.SPRITES_PATH + snakeImages[randomNum]);
   }
}
