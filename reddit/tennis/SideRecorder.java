/**
The side-change rules are:
- Regular games: switch sides after games 1, 3, 5, 7, ... within each set. Count both players' game wins together.
- During a tiebreak: switch sides after every 6 total points: 6, 12, 18, ...
- After a tiebreak: switch sides because it completes the set's 13th game.
- Between sets: don't reset positions. If the completed set had an odd number of games, switch at its end.
  If even, stay until after the first game of the next set.
 */
public class SideRecorder implements MatchListener {

    private Side sideOfA;

    private int gamesPlayedInSet;

    private int tiebreakPointsPlayed;

    public SideRecorder(Side startingSideOfA) {
        this.sideOfA = startingSideOfA;
    }

    @Override
    public void onTiebreakPointPlayed() {
        tiebreakPointsPlayed++;
        if(tiebreakPointsPlayed % 6 == 0) {
            switchSides();
        }
    }

    @Override
    public void onGameCompleted() {
        gamesPlayedInSet++;
        // a tiebreak ending on a multiple of 6 points (e.g. 7-5) already switched on its last point
        boolean alreadySwitched = tiebreakPointsPlayed > 0 && tiebreakPointsPlayed % 6 == 0;
        if(gamesPlayedInSet % 2 == 1 && !alreadySwitched) {
            switchSides();
        }
        tiebreakPointsPlayed = 0;
    }

    @Override
    public void onSetCompleted() {
        // positions carry over; the odd/even rule above already handled the end-of-set switch
        gamesPlayedInSet = 0;
    }

    public Side sideOf(Player player) {
        return player == Player.A ? sideOfA : sideOfA.other();
    }

    private void switchSides() {
        sideOfA = sideOfA.other();
    }
}
