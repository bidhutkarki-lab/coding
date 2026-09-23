import java.util.Map;
import java.util.Optional;

public class Match {
    private static final String[] LABELS = {"love", "15", "30", "40"};

    private final Game game = new Game();

    public void pointWonBy(Player player) {
        game.addScore(player);
    }

    public String score() {
        Optional<String> winner = result();

        if(winner.isPresent()) {
            return winner.get();
        }

        Map<Player, Integer> points = game.getScore();
        int p1 = points.get(Player.A);
        int p2 = points.get(Player.B);

        if(p1 == 3 && p2 == 3) {
            return "deuce";
        }

        if(p1 == 4 && p2 == 3) {
            return "advantage " + Player.A;
        }

        if(p1 == 3 && p2 == 4) {
            return "advantage " + Player.B;
        }

        return LABELS[p1] + "-" + LABELS[p2];
    }

    public Optional<String> result() {

        return game.getResult().map(player -> "winner " + player);

    }
}
