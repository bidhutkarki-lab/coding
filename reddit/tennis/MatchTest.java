public class MatchTest {
    public static void main(String[] args) {
        testMatchFormats();
        testScoreHistory();
        testSideChanges();
        testServing();

        System.out.println("All tests passed");
    }

    private static void testMatchFormats() {
        // Best of 3: A wins, B wins, then A wins.
        Match bo3 = new Match(3);

        check(winSet(bo3, Player.A), null);
        check(bo3.getSetsScore(), "1-0");
        check(bo3.isComplete(), false);

        check(winSet(bo3, Player.B), null);
        check(bo3.getSetsScore(), "1-1");
        check(bo3.getWinner(), null);

        check(winSet(bo3, Player.A), Player.A);
        check(bo3.getSetsScore(), "2-1");
        check(bo3.getWinner(), Player.A);
        check(bo3.isComplete(), true);
        check(
                bo3.getCompletedSetScores(),
                java.util.List.of("6-0", "0-6", "6-0")
        );

        // Best of 5: two set wins aren't enough.
        Match bo5 = new Match(5);

        winSet(bo5, Player.B);
        winSet(bo5, Player.B);

        check(bo5.getSetsScore(), "0-2");
        check(bo5.isComplete(), false);

        check(winSet(bo5, Player.B), Player.B);

        check(bo5.getSetsScore(), "0-3");
        check(bo5.getWinner(), Player.B);

        // Reject points after completion.
        expectThrows(IllegalStateException.class, () -> bo3.recordPoint(Player.B));

        // Reject unsupported formats and missing server.
        expectThrows(IllegalArgumentException.class, () -> new Match(4));
        expectThrows(IllegalArgumentException.class, () -> new Match(3, null));
    }

    private static void testScoreHistory() {
        Match match = new Match(3);
        String initial = match.getState();

        check(match.getPointsPlayed(), 0);
        check(match.getStateAfterPoint(0), initial); // point 0 = initial state

        for (int i = 0; i < 17; i++) {
            match.recordPoint(i % 5 == 0 ? Player.B : Player.A);
        }
        String afterSeventeen = match.getState();
        check(match.getPointsPlayed(), 17);

        winSet(match, Player.A); // keep playing past point 17

        check(match.getStateAfterPoint(0), initial);
        check(match.getStateAfterPoint(17), afterSeventeen);
        check(match.getStateAfterPoint(match.getPointsPlayed()), match.getState());

        expectThrows(IllegalArgumentException.class, () -> match.getStateAfterPoint(-1));
        expectThrows(IllegalArgumentException.class,
                () -> match.getStateAfterPoint(match.getPointsPlayed() + 1));
    }

    private static void testSideChanges() {
        // Change after odd games within a set.
        Match match = new Match(3);
        check(match.getSide(Player.A), Side.NEAR);
        check(match.getSide(Player.B), Side.FAR);

        winGame(match, Player.A); // game 1
        check(match.getSide(Player.A), Side.FAR);
        check(match.getSide(Player.B), Side.NEAR);

        winGame(match, Player.B); // game 2: no change
        check(match.getSide(Player.A), Side.FAR);

        winGame(match, Player.A); // game 3
        check(match.getSide(Player.A), Side.NEAR);

        // Set ends 6-0 (even): no change until game 1 of the next set.
        Match evenSet = new Match(3);
        winSet(evenSet, Player.A); // changes after games 1, 3, 5
        check(evenSet.getSide(Player.A), Side.FAR);
        winGame(evenSet, Player.A);
        check(evenSet.getSide(Player.A), Side.NEAR);

        // Set ends 6-3 (odd): change at the set break AND after game 1 of the next set.
        // A cumulative match-wide game count would get this wrong.
        Match oddSet = new Match(3);
        for (int i = 0; i < 3; i++) {
            winGame(oddSet, Player.B);
        }
        for (int i = 0; i < 6; i++) {
            winGame(oddSet, Player.A);
        }
        check(oddSet.getCompletedSetScores(), java.util.List.of("6-3"));
        check(oddSet.getSide(Player.A), Side.FAR); // changes after games 1, 3, 5, 7, 9
        winGame(oddSet, Player.A);
        check(oddSet.getSide(Player.A), Side.NEAR);

        // Tiebreak: change every 6 points, then again when it ends (game 13).
        Match tb = reachTiebreak();
        check(tb.getSide(Player.A), Side.NEAR); // 12 games: 6 changes

        for (int i = 0; i < 3; i++) {
            tb.recordPoint(Player.A);
            tb.recordPoint(Player.B);
        }
        check(tb.getSide(Player.A), Side.FAR); // 6 tiebreak points

        for (int i = 0; i < 4; i++) {
            tb.recordPoint(Player.A); // wins tiebreak 7-3
        }
        check(tb.getCompletedSetScores(), java.util.List.of("7-6"));
        check(tb.getSide(Player.A), Side.NEAR); // change for game 13

        winGame(tb, Player.A); // game 1 of set 2
        check(tb.getSide(Player.A), Side.FAR);

        // Tiebreak ending on a multiple of 6 (7-5, 12 points) changes only once.
        Match tb12 = reachTiebreak();
        for (int i = 0; i < 5; i++) {
            tb12.recordPoint(Player.A);
            tb12.recordPoint(Player.B);
        }
        check(tb12.getSide(Player.A), Side.FAR); // 10 points: one change at 6
        tb12.recordPoint(Player.A);
        tb12.recordPoint(Player.A); // 7-5 on point 12
        check(tb12.getCompletedSetScores(), java.util.List.of("7-6"));
        check(tb12.getSide(Player.A), Side.NEAR);
    }

    private static void testServing() {
        // Alternates every game and carries across the set boundary.
        Match match = new Match(3, Player.A);
        check(match.getCurrentServer(), Player.A);

        match.recordPoint(Player.B);
        check(match.getCurrentServer(), Player.A); // same server within a game

        winGame(match, Player.B);
        check(match.getCurrentServer(), Player.B);

        winSet(match, Player.A); // set 6-1, 7 games played
        check(match.getCompletedSetScores(), java.util.List.of("6-1"));
        check(match.getCurrentServer(), Player.B); // game 8 overall; no reset at set break

        // First server B.
        Match bFirst = new Match(3, Player.B);
        check(bFirst.getCurrentServer(), Player.B);
        winGame(bFirst, Player.A);
        check(bFirst.getCurrentServer(), Player.A);

        // Tiebreak: first point by the player due to serve, then pairs alternate.
        Match tb = reachTiebreak(); // 12 games played, so A is due to serve
        Player[] expected = {
                Player.A,               // point 1
                Player.B, Player.B,     // points 2-3
                Player.A, Player.A,     // points 4-5
                Player.B, Player.B      // points 6-7
        };
        for (int i = 0; i < expected.length; i++) {
            check(tb.getCurrentServer(), expected[i]);
            tb.recordPoint(i % 2 == 0 ? Player.A : Player.B);
        }

        // After the tiebreak, the player who received first serves the next set.
        Match afterTb = reachTiebreak();
        for (int i = 0; i < 7; i++) {
            afterTb.recordPoint(Player.A);
        }
        check(afterTb.getCompletedSetScores(), java.util.List.of("7-6"));
        check(afterTb.getCurrentServer(), Player.B);

        // No server once the match is over.
        Match done = new Match(3);
        winSet(done, Player.A);
        winSet(done, Player.A);
        expectThrows(IllegalStateException.class, done::getCurrentServer);
    }

    private static Match reachTiebreak() {
        Match match = new Match(3, Player.A);
        for (int i = 0; i < 6; i++) {
            winGame(match, Player.A);
            winGame(match, Player.B);
        }
        return match;
    }

    private static void winGame(Match match, Player player) {
        for (int point = 0; point < 4; point++) {
            match.recordPoint(player);
        }
    }

    private static Player winSet(Match match, Player player) {
        for (int game = 0; game < 6; game++) {
            winGame(match, player);
        }

        return match.getWinner();
    }

    private static void expectThrows(Class<? extends Throwable> type, Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) {
                return;
            }
            throw new AssertionError("Expected " + type.getSimpleName() + ", got " + t, t);
        }
        throw new AssertionError("Expected " + type.getSimpleName() + " to be thrown");
    }

    private static void check(Object actual, Object expected) {
        if (!java.util.Objects.equals(actual, expected)) {
            throw new AssertionError(
                    "Expected: " + expected + ", actual: " + actual
            );
        }
    }
}
