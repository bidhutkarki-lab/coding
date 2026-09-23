import java.util.ArrayList;
import java.util.List;

public class TennisHarness {

    public static void main(String[] args) {
        runTests(args.length > 0 ? args[0] : "AABBABAA");
    }

    /** Plays the sequence on a new best-of-3 match, printing the state after each point. */
    public static Match runTests(String points) {
        return runTests(new Match(3), points, true);
    }

    /**
     * Records each point in order and optionally prints the state after it.
     * Whitespace is ignored so sequences can be grouped by game, e.g. "AAAA BBBB".
     * The whole sequence is validated before any point is recorded.
     */
    public static Match runTests(Match match, String points, boolean print) {
        for (Player player : parse(points)) {
            match.recordPoint(player);
            if (print) {
                System.out.println(match.getPointsPlayed() + ". " + player + " -> " + match.getState());
            }
        }
        return match;
    }

    private static List<Player> parse(String points) {
        List<Player> players = new ArrayList<>();
        for (char c : points.toCharArray()) {
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c == 'A') {
                players.add(Player.A);
            } else if (c == 'B') {
                players.add(Player.B);
            } else {
                throw new IllegalArgumentException("Unknown player '" + c + "', expected A or B");
            }
        }
        return players;
    }
}
