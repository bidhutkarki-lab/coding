import java.util.EnumMap;
import java.util.Map;

public class TennisSet {

    private final Map<Player, Integer> games = new EnumMap<>(Player.class);

    private Game currentGame;
    private TiebreakGame currentTiebreak; // null unless a tiebreak is in progress
    private Player winner;

    public TennisSet() {
        games.put(Player.A, 0);
        games.put(Player.B, 0);
        this.currentGame = new Game();
    }

    public void recordPoint(Player player) {
        if(winner != null) {
            throw new IllegalStateException("Set is already complete");
        }

        if(isTiebreakActive()) {
            currentTiebreak.recordPoint(player);
            Player tiebreakWinner = currentTiebreak.getWinner();
            if(tiebreakWinner != null) {
                addGameWin(tiebreakWinner);
                winner = tiebreakWinner;
                currentTiebreak = null;
            }
            return;
        }

        currentGame.recordPoint(player);
        Player gameWinner = currentGame.getWinner();
        if(gameWinner == null) {
            return;
        }

        // game is complete
        addGameWin(gameWinner);
        int g1 = games.get(Player.A);
        int g2 = games.get(Player.B);

        if(hasSetWon()) {
            winner = g1 > g2 ? Player.A : Player.B;
        } else if(g1 == 6 && g2 == 6) {
            currentTiebreak = new TiebreakGame();
        } else {
            currentGame = new Game();
        }
    }

    /** Games won by the given player */
    public int getGamesWon(Player player) {
        return games.get(player);
    }

    /** Total games completed in this set by both players; a finished tiebreak counts as one. */
    public int getGamesPlayed() {
        return games.get(Player.A) + games.get(Player.B);
    }

    private void addGameWin(Player player) {
        games.merge(player, 1, Integer::sum);
    }

    private boolean hasSetWon() {
        int g1 = games.get(Player.A);
        int g2 = games.get(Player.B);
        return Math.max(g1, g2) >= 6
                && Math.abs(g1 - g2) >= 2;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isTiebreakActive() {
        return currentTiebreak != null;
    }

    public String getGamesScore() {
        return games.get(Player.A) + "-" + games.get(Player.B);
    }

    public String getScore() {
        if(winner != null) {
            return "Set: " + getGamesScore() + ", winner " + winner;
        }

        if(isTiebreakActive()) {
            return "Set: " + getGamesScore() + ", Tiebreak: " + currentTiebreak.getScore();
        }

        return "Set: " + getGamesScore() + ", game: " + currentGame.getScore();
    }

    // For managing serve

    // Points played so far in the active tiebreak, 0 if no tiebreak is active
    public int getTiebreakPointsPlayed() {
        return isTiebreakActive() ? currentTiebreak.getTotalPoints() : 0;
    }
}
