import java.util.*;

public class Game {

    private final EnumMap<Player, Integer> points = new EnumMap<>(Player.class);

    public Game() {
        points.put(Player.A, 0);
        points.put(Player.B, 0);
    }

    public void addScore(Player player) {

        Objects.requireNonNull(player, "Player is required");

        if(getResult().isPresent()) {
            throw new IllegalStateException("Game Over");
        }

        points.merge(player, 1, Integer::sum);

        int p1 = points.get(Player.A);
        int p2 = points.get(Player.B);

        // reset after duece
        if(p1 == p2 && p1 >= 4) {
            points.put(Player.A, 3);
            points.put(Player.B, 3);
        }

    }

    public Map<Player, Integer> getScore() {
        // return a copy so callers cannot change the game's position
        return new EnumMap<>(points);
    }

    public Optional<Player> getResult() {

        int p1 = points.get(Player.A);
        int p2 = points.get(Player.B);

        if(p1 >= 4 && p1-p2 >= 2) {
            return Optional.of(Player.A);
        }

        if(p2 >= 4 && p2-p1 >= 2) {
            return Optional.of(Player.B);
        }

        return Optional.empty();
    }
}
