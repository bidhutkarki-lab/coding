import java.util.*;

public class TennisSet {

    private final Score gameScore = new Score();

    private Game currentGame;
    private TiebreakGame currentTiebreakGame;

    private final List<MatchListener> listeners = MatchListenerFactory.getListeners();

    public TennisSet() {
    }

    public void recordPoint(Player player) {

        if(getWinner() != null) {
            throw new IllegalStateException("Set has been already over");
        }

        if(currentTiebreakGame != null) {
            // tie break is active, record it's point
            currentTiebreakGame.recordPoint(player);
            listeners.forEach(MatchListener::onTiebreakPointPlayed);
            Player gameWinner = currentTiebreakGame.getWinner();
            if(gameWinner != null) {
                gameScore.add(gameWinner);
                currentTiebreakGame = null;
                listeners.forEach(MatchListener::onGameCompleted);
                // winning the tiebreak always wins the set (7-6)
                listeners.forEach(MatchListener::onSetCompleted);
            }
            return;
        }

        currentGame.recordPoint(player);
        Player gameWinner = currentGame.getWinner();
        if(gameWinner == null) {
            return;
        }

        // game is complete at this point
        gameScore.add(gameWinner);
        listeners.forEach(MatchListener::onGameCompleted);
        Player winner = getWinner();
        if(winner != null) {
            listeners.forEach(MatchListener::onSetCompleted);
            return;
        }

        if(gameScore.get(Player.A) == 6 && gameScore.get(Player.B) == 6) {
            currentTiebreakGame = new TiebreakGame();
        } else {
            currentGame = new Game();
        }
    }

    public Player getWinner() {
        Player winner = gameScore.getWinnerByTwo(6);
        if(winner != null) {
            return winner;
        }

        // special case for tiebreak winner
        int g1 = gameScore.get(Player.A), g2 = gameScore.get(Player.B);
        if(g1 ==7 && g2 == 6) {
            return Player.A;
        }
        if(g2 == 7 && g1 == 6) {
            return Player.B;
        }
        return null;
    }

    public String getScore() {
        StringBuilder sb = new StringBuilder("Set: ").append(gameScore);

        if (getWinner() != null) {
            return sb.append(", winner: ").append(getWinner()).toString();
        }

        if (currentTiebreakGame != null) {
            sb.append(", tiebreak: ").append(currentTiebreakGame.getScore());
        } else {
            sb.append(", game: ").append(currentGame.getScore());
        }
        return sb.toString();
    }
}
