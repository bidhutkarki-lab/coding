import java.util.*;

public class Game {

    private static final String[] LABELS = {"0", "15", "30", "40"};

    private final Map<Player, Integer> points = new EnumMap<>(Player.class);

    public Game() {
        points.put(Player.A, 0);
        points.put(Player.B, 0);
    }

    public void recordPoint(Player player) {
        Objects.requireNonNull(player, "Player is required");

        if(getWinner() != null) {
            throw new IllegalStateException("Game is already over");
        }

        points.merge(player, 1, Integer::sum);
    }

    public Player getWinner() {
        int p1 = points.get(Player.A);
        int p2 = points.get(Player.B);

        if(p1 >=4 && p1 - p2 >= 2) {
            return Player.A;
        }

        if(p2 >= 4 && p2 - p1 >= 2) {
            return Player.B;
        }

        return null;
    }

    public String getScore() {
        Player winner = getWinner();

        if(winner != null) {
            return "winner " + winner;
        }

        int p1 = points.get(Player.A);
        int p2 = points.get(Player.B);

        if(p1 >= 3 && p2 >= 3) {
            if(p1 == p2) {
                return "deuce";
            }
            return "advantage " + (p1 > p2 ? Player.A : Player.B);
        }

        return LABELS[p1] + "-" + LABELS[p2];
    }

}
