package snakeladder.utility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import snakeladder.game.*;
import static java.lang.Math.acos;

public class BackgroundDrawing {
    public static final String SPRITES_PATH = "src/sprites/";

    private static double calculateDistance(Point pointOne, Point pointTwo) {
        double distance = Math.sqrt((pointTwo.getX() - pointOne.getX()) * (pointTwo.getX() - pointOne.getX()) +
                (pointTwo.getY() - pointOne.getY()) * (pointTwo.getY() - pointOne.getY()));
        return distance;
    }

    private static double calculateAngleRadian(Point pointOne, Point pointTwo, Point pointThree) {
        double distanceOneTwo = calculateDistance(pointTwo, pointOne);
        double distanceOneThree = calculateDistance(pointThree, pointOne);
        double distanceTwoThree = calculateDistance(pointTwo, pointThree);

        double distanceOneTwoSquare = distanceOneTwo * distanceOneTwo;
        double distanceOneThreeSquare = distanceOneThree * distanceOneThree;
        double distanceTwoThreeSquare = distanceTwoThree * distanceTwoThree;

        // From Cosine law
        double angleOne = acos((distanceOneTwoSquare + distanceOneThreeSquare - distanceTwoThreeSquare)/(2 * distanceOneTwo * distanceOneThree));
        return angleOne;
    }
    private static int pixelLocation(double percentX, int width) {
        return (int) (percentX * width);
    }

    public static void addImageToBackground(String backgroundImagePath, java.util.List<Connection>imageConnections,
                                            int numberCellsHorizontal, int numberCellVertical, String outputImagePath) {
        BufferedImage backgroundImage = null;
        try {
            System.out.println("backgroundImagePath = " + backgroundImagePath);
            backgroundImage = ImageIO.read(new File(backgroundImagePath));

            int width = backgroundImage.getWidth(), height = backgroundImage.getHeight();

            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ig2 = bi.createGraphics();
            ig2.drawImage(backgroundImage, null,0, 0);
            for (Connection connection : imageConnections) {

                double startXPercent = connection.xLocationPercent(connection.getLocStart().x);
                double startYPercent = connection.yLocationPercent(connection.getLocStart().y);

                double endXPercent = connection.xLocationPercent(connection.getLocEnd().x);
                double endYPercent = connection.yLocationPercent(connection.getLocEnd().y);

                double halfCellWidth = width / (numberCellsHorizontal * 2);
                double halfCellHeight = height / (numberCellVertical * 2);
                int startXPixel = pixelLocation(startXPercent, width);
                int startYPixel = pixelLocation(startYPercent, height);
                int endXPixel = pixelLocation(endXPercent, width);
                int endYPixel = pixelLocation(endYPercent, height);

                Point startPoint = new Point(startXPixel, startYPixel);
                Point endPoint = new Point(endXPixel, endYPixel);
                int directionMultiplicity = 0;
                boolean isStraightUp = false;
                if (startXPixel != endXPixel) {
                    directionMultiplicity = (startXPixel - endXPixel) / Math.abs(startXPixel - endXPixel);
                } else {
                    if (startYPixel > endYPixel) {
                        isStraightUp = true;
                    }
                }
                double distance = calculateDistance(startPoint, endPoint);
                Point projectionPoint = new Point(startXPixel, startYPixel + (int) distance);
                double angleRadian = calculateAngleRadian(startPoint, endPoint, projectionPoint);

                BufferedImage connectionImage = ImageIO.read(new File(connection.getImagePath()));
                double heightScale = distance / connectionImage.getHeight();
                AffineTransform trans = new AffineTransform();
                trans.translate(startXPixel + halfCellWidth, startYPixel + halfCellHeight);
                if (isStraightUp) {
                    trans.rotate( Math.PI);
                } else {
                    trans.rotate(directionMultiplicity * angleRadian);
                }

                if (directionMultiplicity != 0) {
                    trans.scale(-1 * directionMultiplicity, heightScale);
                } else {
                    trans.scale(1, heightScale);
                }


                ig2.drawImage(connectionImage, trans, null);
                System.out.println("add foreground image");
            }

            ImageIO.write(bi, "PNG", new File(outputImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
