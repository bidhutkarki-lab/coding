import java.util.Optional;

public class Match {

    private static final String[] LABELS = {"love", "15", "30", "40"};

    private final String player1;
    private final String player2;
    private final Game game;

    public Match(String player1, String player2) {
        this.game = new Game(player1, player2);
        this.player1 = player1;
        this.player2 = player2;
    }

    public void pointWonBy(String player) {
        game.addScore(player);
    }

    public String score() {
        Optional<String> result = result();
        if (result.isPresent()) {
            return result.get();
        }

        Score s = game.getScore();
        int p1 = s.player1Points();
        int p2 = s.player2Points();

        if (p1 >= 3 && p2 >= 3) {
            if (p1 == p2) {
                return "deuce";
            }
            return "advantage " + (p1 > p2 ? player1 : player2);
        }
        return LABELS[p1] + "-" + LABELS[p2];
    }

    public Optional<String> result() {
        return game.getResult().map(winner -> "winner " + winner);
    }
}
