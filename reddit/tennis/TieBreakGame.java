import java.util.EnumMap;
import java.util.Map;

public class TiebreakGame {
    private final Map<Player, Integer> points = new EnumMap<>(Player.class);

    public TiebreakGame() {
        points.put(Player.A, 0);
        points.put(Player.B, 0);
    }

    public void recordPoint(Player player) {
        if(getWinner() != null) {
            throw new IllegalStateException("Tiebreak is already over");
        }

        points.merge(player, 1, Integer::sum);
    }

    public Player getWinner() {

        int a = points.get(Player.A);
        int b = points.get(Player.B);

        if(a >=7 && a-b >= 2) {
            return Player.A;
        }

        if(b >=7 && b - a >= 2) {
            return Player.B;
        }

        return null;
    }

    public String getScore() {
        return points.get(Player.A) + "-" + points.get(Player.B);
    }

    /** For managing the server. */
    public int getTotalPoints() {
        return points.get(Player.A) + points.get(Player.B);
    }
}
