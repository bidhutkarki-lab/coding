public class MatchTest {
    public static void main(String[] args) {
        // Best of 3: A wins, B wins, then A wins.
        Match bo3 = new Match(3);

        winSet(bo3, Game.Player.A);
        check(bo3.getSetsScore(), "1-0");
        check(bo3.isComplete(), false);

        winSet(bo3, Game.Player.B);
        check(bo3.getSetsScore(), "1-1");
        check(bo3.getWinner(), null);

        winSet(bo3, Game.Player.A);
        check(bo3.getSetsScore(), "2-1");
        check(bo3.getWinner(), Game.Player.A);
        check(bo3.isComplete(), true);
        check(
                bo3.getCompletedSetScores(),
                java.util.List.of("6-0", "0-6", "6-0")
        );

        // Best of 5: two set wins aren't enough.
        Match bo5 = new Match(5);

        winSet(bo5, Game.Player.B);
        winSet(bo5, Game.Player.B);

        check(bo5.getSetsScore(), "0-2");
        check(bo5.isComplete(), false);

        winSet(bo5, Game.Player.B);

        check(bo5.getSetsScore(), "0-3");
        check(bo5.getWinner(), Game.Player.B);

        // Reject points after completion.
        try {
            bo3.recordPoint(Game.Player.B);
            throw new AssertionError("Expected completed match rejection");
        } catch (IllegalStateException expected) {
            // Expected.
        }

        // Reject unsupported formats.
        try {
            new Match(4);
            throw new AssertionError("Expected invalid format rejection");
        } catch (IllegalArgumentException expected) {
            // Expected.
        }

        System.out.println("All tests passed");
    }

    private static void winSet(Match match, Game.Player player) {
        for (int game = 0; game < 6; game++) {
            for (int point = 0; point < 4; point++) {
                match.recordPoint(player);
            }
        }
    }

    private static void check(Object actual, Object expected) {
        if (!java.util.Objects.equals(actual, expected)) {
            throw new AssertionError(
                    "Expected: " + expected + ", actual: " + actual
            );
        }
    }
}
