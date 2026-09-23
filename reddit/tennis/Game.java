import java.util.EnumMap;
import java.util.Map;

public class Game {

    private static final String[] LABELS = {"0", "15", "30", "40"};

    private final Map<Player, Integer> points = new EnumMap<>(Player.class);

    public Game() {
        points.put(Player.A, 0);
        points.put(Player.B, 0);
    }

    public void recordPoint(Player player) {
        if(getWinner() != null) {
            throw new IllegalStateException("Game is already over");
        }

        points.merge(player, 1, Integer::sum);
    }

    public Player getWinner() {
        int a = points.get(Player.A);
        int b = points.get(Player.B);

        if(a >=4 && a - b >= 2) {
            return Player.A;
        }

        if(b >= 4 && b - a >= 2) {
            return Player.B;
        }

        return null;
    }

    public String getScore() {
        Player winner = getWinner();

        if(winner != null) {
            return "winner " + winner;
        }

        int a = points.get(Player.A);
        int b = points.get(Player.B);

        if(a >= 3 && b >= 3) {
            if(a == b) {
                return "deuce";
            }
            return "advantage " + (a > b ? Player.A : Player.B);
        }

        return LABELS[a] + "-" + LABELS[b];
    }

    public int getTotalPoints() {
        return points.get(Player.A) + points.get(Player.B);
    }

}
