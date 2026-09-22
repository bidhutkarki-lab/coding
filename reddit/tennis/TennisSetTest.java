public class TennisSetTest {
    public static void main(String[] args) {
        // Set ends at 6-4.
        TennisSet normal = new TennisSet();
        reachFiveFour(normal);

        winGame(normal, Player.A);

        check(normal.getGamesScore(), "6-4");
        check(normal.getWinner(), Player.A);

        // Set continues at 6-5, then ends at 7-5.
        TennisSet extended = new TennisSet();
        reachFiveFour(extended);

        winGame(extended, Player.B); // 5-5
        winGame(extended, Player.A); // 6-5

        check(extended.getWinner(), null);
        check(extended.isTieBreakActive(), false);

        winGame(extended, Player.A); // 7-5

        check(extended.getGamesScore(), "7-5");
        check(extended.getWinner(), Player.A);

        // At 6-6, start a TieBreak.
        TennisSet tied = new TennisSet();

        for (int i = 0; i < 6; i++) {
            winGame(tied, Player.A);
            winGame(tied, Player.B);
        }

        check(tied.getGamesScore(), "6-6");
        check(tied.isTieBreakActive(), true);

        // TieBreak reaches 6-6.
        for (int i = 0; i < 6; i++) {
            tied.recordPoint(Player.A);
            tied.recordPoint(Player.B);
        }

        tied.recordPoint(Player.A); // 7-6: keep playing
        check(tied.getWinner(), null);

        tied.recordPoint(Player.A); // 8-6: A wins
        check(tied.getGamesScore(), "7-6");
        check(tied.getWinner(), Player.A);
        check(tied.isTieBreakActive(), false);

        try {
            tied.recordPoint(Player.B);
            throw new AssertionError("Expected rejection after set completion");
        } catch (IllegalStateException expected) {
            // Expected.
        }

        System.out.println("All tests passed");
    }

    private static void reachFiveFour(TennisSet set) {
        for (int i = 0; i < 4; i++) {
            winGame(set, Player.A);
            winGame(set, Player.B);
        }
        winGame(set, Player.A);
    }

    private static void winGame(TennisSet set, Player player) {
        for (int i = 0; i < 4; i++) {
            set.recordPoint(player);
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
