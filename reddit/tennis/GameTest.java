public class GameTest {
    public static void main(String[] args) {
        Game game = new Game();

        check(game.getScore(), "0-0");

        game.recordPoint(Player.A);
        game.recordPoint(Player.A);

        check(game.getScore(), "30-0");
        check(game.getWinner(), null); // A two-point lead alone isn't a win.

        game.recordPoint(Player.A);
        game.recordPoint(Player.B);
        game.recordPoint(Player.B);
        game.recordPoint(Player.B);
        check(game.getScore(), "deuce");

        game.recordPoint(Player.A);
        check(game.getScore(), "advantage A");

        game.recordPoint(Player.B);
        check(game.getScore(), "deuce"); // Actual counts are now 4-4.

        game.recordPoint(Player.B);
        check(game.getScore(), "advantage B");

        game.recordPoint(Player.B);
        check(game.getScore(), "winner B");
        check(game.getWinner(), Player.B);

        try {
            game.recordPoint(Player.A);
            throw new AssertionError("Expected completed game to reject points");
        } catch (IllegalStateException expected) {
            // Expected.
        }

        Game straightWin = new Game();
        for (int i = 0; i < 4; i++) {
            straightWin.recordPoint(Player.A);
        }
        check(straightWin.getWinner(), Player.A);

        System.out.println("All tests passed");
    }

    private static void check(Object actual, Object expected) {
        if (!java.util.Objects.equals(actual, expected)) {
            throw new AssertionError(
                "Expected: " + expected + ", actual: " + actual
            );
        }
    }
}
