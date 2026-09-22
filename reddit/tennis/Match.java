import java.util.*;

public class Match {
    private final int bestOf;
    private final int setsNeeded;

    private int setsA;
    private int setsB;

    private TennisSet currentSet = new TennisSet();
    private final List<String> completedSetScores = new ArrayList<>();

    private Player winner;

    public Match(int bestOf) {

        if(bestOf != 3 && bestOf != 5) {
            throw new IllegalArgumentException("Best of must be 3 or 5");
        }

        this.bestOf = bestOf;
        this.setsNeeded = bestOf / 2 + 1;
    }

    public void recordPoint(Player player) {
        if(player == null) {
            throw new IllegalArugmentException("Player is required");
        }

        if(isComplete()) {
            throw new IllegalStateException("Match is already complete");
        }

        currentSet.recordPoint(player);

        Player setWinner = currentSet.getWinner();
        if(setWinner == null) {
            return;
        }

        completedSetScores.add(currentSet.getGamesScore());

        if(setWinner == Player.A) {
            setsA++;
        } else {
            setsB++;
        }

        if(setsA == setsNeeded) {
            winner = Player.A;
        } else if(setsB == setsNeeded) {
            winner = Player.B;
        }

        if(!isComplete()) {
            currentSet = new TennisSet();
        }
    }

    public boolean isComplete() {
        return winner != null;
    }

    public Player getWinner() {
        return winner;
    }

    public String getSetsScore() {
        return setsA + "-" + setsB;
    }

    public List<String> getCompletedSetScores() {
        // prevent callers from modifying the match's internal list
        return List.copyOf(completedSetScores);
    }

    public String getState() {
        String state = "Best of " + bestOf + ", sets: " + getSetsScore() + ", completed sets: " + completedSetScores;

        if(isComplete()) {
            return state + ", winner " + winner;
        }

        return state + ", current " + currentSet.getScore();
    }
}
