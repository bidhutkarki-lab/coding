import java.util.EnumMap;
import java.util.Map;

public class TennisSet {

    private final Map<Player, Integer> games = new EnumMap<>(Player.class);

    private Game currentGame;
    private TiebreakGame currentTiebreak;
    private boolean tiebreakActive = false;
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

        if(tiebreakActive) {
            currentTiebreak.recordPoint(player);
            Player tiebreakWinner = currentTiebreak.getWinner();
            if(tiebreakWinner != null) {
                addGameWin(tiebreakWinner);
                winner = tiebreakWinner;
                tiebreakActive = false;
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
        int a = games.get(Player.A);
        int b = games.get(Player.B);

        if(hasSetWon()) {
            winner = a > b ? Player.A : Player.B;
        } else if(a == 6 && b == 6) {
            tiebreakActive = true;
            currentTiebreak = new TiebreakGame();
        } else {
            currentGame = new Game();
        }
    }

    /** Games won by the given player */
    public int getGamesWon(Player player) {
        return games.get(player);
    }

    public void addGameWin(Player player) {
        games.merge(player, 1, Integer::sum);
    }

    private boolean hasSetWon() {
        int gamesA = games.get(Player.A);
        int gamesB = games.get(Player.B);
        return Math.max(gamesA, gamesB) >= 6
                && Math.abs(gamesA - gamesB) >= 2;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isTiebreakActive() {
        return tiebreakActive;
    }

    public String getGamesScore() {
        return games.get(Player.A) + "-" + games.get(Player.B);
    }

    public String getScore() {
        if(winner != null) {
            return "Set:" + getGamesScore() + ", winner " + winner;
        }

        if(isTiebreakActive()) {
            return "Set: " + getGamesScore() + ", Tiebreak: " + currentTiebreak.getScore();
        }

        return "Set: " + getGamesScore() + ", game: " + currentGame.getScore();
    }

    // For managing serve

    // Points played so far in the active tiebreak, 0 if no tiebreak is active
    public int getTiebreakPointsPlayed() {
        return tiebreakActive ? currentTiebreak.getTotalPoints() : 0;
    }
}
