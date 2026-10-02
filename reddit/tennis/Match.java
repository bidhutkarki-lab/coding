import java.util.*;

public class Match {

    private final Score setScore = new Score();

    private final MatchFormat matchFormat;

    private TennisSet currentSet = new TennisSet();

    private ServerRecorder serverRecorder = MatchListenerFactory.getServerRecorder();

    private SideRecorder sideRecorder = MatchListenerFactory.getSideRecorder();

    public Match(MatchFormat matchFormat) {
        this.matchFormat = matchFormat;
    }

    public void recordPoint(Player player) {

        if(getWinner() != null) {
            throw new IllegalStateException("Match is already over");
        }

        currentSet.recordPoint(player);
        Player setWinner = currentSet.getWinner();
        if(setWinner == null) {
            // set is ongoing
            return;
        }

        // set is complete
        setScore.add(setWinner);
        Player matchWinner = getWinner();
        if(matchWinner != null) {
            return; // match won
        }

        currentSet = new TennisSet();
    }

    public Player getServer() {
        if(isComplete()) {
            throw new IllegalStateException("Match is already over");
        }
        return serverRecorder.getServer();
    }

    public Side getSide(Player player) {
        return sideRecorder.sideOf(player);
    }

    public Player getWinner() {
        int s1 = setScore.get(Player.A), s2 = setScore.get(Player.B);

        if(s1 >= matchFormat.setsNeeded()) {
            return Player.A;
        } else if(s2 >= matchFormat.setsNeeded()) {
            return Player.B;
        }

        return null;
    }

    public String getScore() {
        return setScore.toString();
    }

    public boolean isComplete() {
        return getWinner() != null;
    }

}
