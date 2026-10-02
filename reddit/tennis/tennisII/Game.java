import java.util.Objects;
import java.util.Optional;

public class Game {

    private final String player1;
    private final String player2;
    private int p1;
    private int p2;

    public Game(String player1, String player2) {
        Objects.requireNonNull(player1, "player1 is required");
        Objects.requireNonNull(player2, "player2 is required");
        if (player1.isBlank() || player2.isBlank()) {
            throw new IllegalArgumentException("Player names must be nonblank");
        }
        if (player1.equals(player2)) {
            throw new IllegalArgumentException("Player names must be distinct");
        }
        this.player1 = player1;
        this.player2 = player2;
    }

    public void addScore(String player) {
        if (getResult().isPresent()) {
            throw new IllegalStateException("Game is already over");
        }
        if (player1.equals(player)) {
            p1++;
        } else if (player2.equals(player)) {
            p2++;
        } else {
            throw new IllegalArgumentException("Unknown player: " + player);
        }

        if (p1 == p2 && p1 >= 4) {
            p1 = 3;
            p2 = 3;
        }
    }

    public Score getScore() {
        return new Score(p1, p2);
    }

    public Optional<String> getResult() {
        if (p1 >= 4 && p1 - p2 >= 2) {
            return Optional.of(player1);
        }
        if (p2 >= 4 && p2 - p1 >= 2) {
            return Optional.of(player2);
        }
        return Optional.empty();
    }
}
