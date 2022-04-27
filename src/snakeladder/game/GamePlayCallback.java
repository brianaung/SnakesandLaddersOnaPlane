package snakeladder.game;

public interface GamePlayCallback {
    void finishGameWithResults(int winningPlayerIndex, java.util.List<String> playerCurrentPositions);
}
