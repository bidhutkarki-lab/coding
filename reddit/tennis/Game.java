import java.util.*;

public class Game {

    private static final String[] LABELS = {"0", "15", "30", "40"};

    private final Score points = new Score();

    public void recordPoint(Player player) {
        Objects.requireNonNull(player, "Player is required");

        if(getWinner() != null) {
            throw new IllegalStateException("Game is already over");
        }

        points.add(player);
    }

    public Player getWinner() {
        return points.getWinnerByTwo(4);
    }

    public String getScore() {
        Player winner = getWinner();
        if (winner != null) {
            return "Game " + winner;
        }

        int p1 = points.get(Player.A);
        int p2 = points.get(Player.B);

        if(p1 >= 3 && p2 >= 3) {
            if(p1 == p2) {
                return "Deuce";
            } else {
                return "Advantage " + (p1 > p2 ? Player.A : Player.B);
            }
        }

        return LABELS[p1] + "-" + LABELS[p2];
    }
}
