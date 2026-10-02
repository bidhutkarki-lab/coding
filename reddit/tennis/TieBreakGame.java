import java.util.*;

public class TiebreakGame {

    private Score points = new Score();

    public void recordPoint(Player player) {

        Objects.requireNonNull(player, "Player is required");

        if(getWinner() != null) {
            throw new IllegalStateException("Tiebreak game is over");
        }

        points.add(player);
    }

    public Player getWinner() {
       return points.getWinnerByTwo(7);
    }

    public String getScore() {
        return points.toString();
    }
}
