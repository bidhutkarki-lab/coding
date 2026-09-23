import java.util.*;

public class Match {
    private final int bestOf;
    private final int setsNeeded;

    private final Map<Player, Integer> setsWon = new EnumMap<>(Player.class);

    private TennisSet currentSet;
    private final List<TennisSet> completedSets = new ArrayList<>();

    private Player winner = null;

    private final Player firstServer;

    private final List<Player> pointHistory = new ArrayList<>();

    private final SideTracker sides = new SideTracker(Side.NEAR);

    public Match(int bestOf) {
        this(bestOf, Player.A);
    }

    public Match(int bestOf, Player firstServer) {

        if(bestOf != 3 && bestOf != 5) {
            throw new IllegalArgumentException("Best of must be 3 or 5");
        }

        if(firstServer == null) {
            throw new IllegalArgumentException("First server is required");
        }

        this.bestOf = bestOf;
        this.setsNeeded = bestOf / 2 + 1;
        setsWon.put(Player.A, 0);
        setsWon.put(Player.B, 0);
        this.currentSet = new TennisSet();
        this.firstServer = firstServer;
    }

    public void recordPoint(Player player) {
        if(winner != null) {
            throw new IllegalStateException("Match is already complete");
        }

        int gamesBefore = currentSet.getGamesPlayed();

        currentSet.recordPoint(player);
        pointHistory.add(player);

        int gamesAfter = currentSet.getGamesPlayed();

        if(gamesAfter > gamesBefore) {
            // when a game is complete
            sides.onGameCompleted(gamesAfter);
        } else if(currentSet.isTiebreakActive()) {
            sides.onTiebreakPointPlayed(currentSet.getTiebreakPointsPlayed());
        }


        Player setWinner = currentSet.getWinner();
        if(setWinner == null) {
            return;
        }

        // set is complete
        setsWon.merge(setWinner, 1, Integer::sum);

        completedSets.add(currentSet);

        if(hasWonMatch(setWinner)) {
            winner = setWinner;
        } else {
            currentSet = new TennisSet();
        }
    }

    private boolean hasWonMatch(Player player) {
        return setsWon.get(player) == setsNeeded;
    }

    public Side getSide(Player player) {
        return sides.sideOf(player);
    }

    public boolean isComplete() {
        return winner != null;
    }

    public Player getWinner() {
        return winner;
    }

    public String getSetsScore() {
        return setsWon.get(Player.A) + "-" + setsWon.get(Player.B);
    }

    public List<String> getCompletedSetScores() {
        List<String> scores = new ArrayList<>();
        for(TennisSet set : completedSets) {
            scores.add(set.getGamesScore());
        }
        return scores;
    }

    public String getState() {
        String state = "Best of " + bestOf + ", sets: " + getSetsScore() + ", completed sets: " + getCompletedSetScores();

        if(isComplete()) {
            return state + ", winner " + winner;
        }

        return state + ", current " + currentSet.getScore();
    }

    /**
     * Regular games: serve alternates every game across the whole match
     * Tiebreak: the player due to serve takes point 1, then players alternate every 2 points (2-3, 4-5, ...).
     * After a tiebreak: it counts as one game, so the player who received first in it serves the next set.
     */
    public Player getCurrentServer() {
        if(isComplete()) {
            throw new IllegalStateException("Game is over");
        }
        Player gameServer = (totalCompletedGamesInMatch() % 2 == 0 ? firstServer : firstServer.opponent());

        int tiebreakPoints = currentSet.getTiebreakPointsPlayed();
        if(tiebreakPoints == 0) {
            return gameServer;
        }

        // Tiebreak serving order (S = gameServer, O = opponent):
        //   point #:  1 | 2  3 | 4  5 | 6  7 | 8  9 ...
        //   server:   S | O  O | S  S | O  O | S  S ...
        // Point 1 is a lone serve; after that, serve changes every 2 points.
        //
        // tiebreakPoints = points already played, so we're picking the server of point (tiebreakPoints + 1).
        //   - 1 : drop the lone first point so the rest line up in pairs
        //   / 2 : number the pairs 0, 1, 2, ... (even pair -> O, odd pair -> S)
        //
        //   played | next point | (played-1)/2 | server
        //     1    |     2      |      0       |   O
        //     2    |     3      |      0       |   O
        //     3    |     4      |      1       |   S
        //     4    |     5      |      1       |   S
        //     5    |     6      |      2       |   O
        // (played == 0 -> point 1 -> S, handled by the early return above.)
        int pairIndex = (tiebreakPoints - 1) / 2;
        return (pairIndex % 2 == 0) ? gameServer.opponent() : gameServer;
    }

    private int totalCompletedGamesInMatch() {
        int sum = 0;
        for(TennisSet set : completedSets) {
            sum += set.getGamesPlayed();
        }
        sum += currentSet.getGamesPlayed();
        return sum;
    }


    public int getPointsPlayed() {
        return pointHistory.size();
    }

    /** Point 0 is the initial state; point n is the state right after the nth point */
    public String getStateAfterPoint(int point) {
        if(point < 0 || point > pointHistory.size()) {
            throw new IllegalArgumentException("Points must be between 0 and " + getPointsPlayed());
        }

        // Replay takes time,
        // you can use snapshot of getState() in a hashmap, tradeoff: just keeps one string format
        Match replay = new Match(bestOf, firstServer);
        for(int i=0; i<point; i++) {
            replay.recordPoint(pointHistory.get(i));
        }
        return replay.getState();
    }
}
