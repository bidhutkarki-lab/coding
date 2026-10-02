import java.util.EnumMap;
import java.util.Map;

public class Score {

    private final Map<Player, Integer> score = new EnumMap<>(Player.class);

    public Score() {
        score.put(Player.A, 0);
        score.put(Player.B, 0);
    }

    public void add(Player player) {
        score.merge(player, 1, Integer::sum);
    }

    public int get(Player player) {
        return score.get(player);
    }

    public Player getWinnerByTwo(int min) {
        int p1 = score.get(Player.A);
        int p2 = score.get(Player.B);

        if(p1 >= min && p1 - p2 >= 2) {
            return Player.A;
        } else if(p2 >= min && p2 - p1 >= 2) {
            return Player.B;
        }

        return null;
    }

    @Override
    public String toString() {
        return score.get(Player.A) + "-" + score.get(Player.B);
    }
}
