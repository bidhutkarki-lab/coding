/**
 *
 * Regular games: switch sides after games 1, 3, 5, 7, … within each set. Count both players’ game wins together.
 * During a tiebreak: switch sides after every 6 total points: 6, 12, 18, …
 * After a tiebreak: switch sides because it completes the set’s 13th game.
 */
public class SideTracker {
    private Side sideOfA;

    public SideTracker(Side startingSideOfA) {
        this.sideOfA = startingSideOfA;
    }

    public void onGameCompleted(int gamesPlayedInSet) {
        if(gamesPlayedInSet % 2 == 1) {
            sideOfA = sideOfA.other();
        }
    }

    public void onTiebreakPointPlayed(int tiebreakPointsPlayed) {
        if(tiebreakPointsPlayed % 6 == 0) {
            sideOfA = sideOfA.other();
        }
    }

    public Side sideOf(Player player) {
        return player == Player.A ? sideOfA : sideOfA.other();
    }
}
