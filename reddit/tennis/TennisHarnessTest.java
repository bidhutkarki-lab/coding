public class TennisHarnessTest {

    private static final String GAME_A = "AAAA ";
    private static final String GAME_B = "BBBB ";
    private static final String SET_A = GAME_A.repeat(6);
    private static final String SET_B = GAME_B.repeat(6);

    private static final String BO3_START = "Best of 3, sets: 0-0, completed sets: [], current ";

    public static void main(String[] args) {
        testExampleSequence();
        testGameWithoutDeuce();
        testDeuceAdvantageDeuce();
        testDeuceAdvantageGame();
        testSetEndsSixFour();
        testSetContinuesAtSixFiveEndsSevenFive();
        testTiebreakBeginsAtSixSix();
        testTiebreakContinuesAtSevenSixEndsEightSix();
        testBestOfThreeEndsAfterTwoSets();
        testBestOfFiveEndsAfterThreeSets();
        testPointAfterMatchCompletionRejected();
        testInvalidInputRejected();

        System.out.println("All tests passed");
    }

    private static void testExampleSequence() {
        // 15-0, 30-0, 30-15, 30-30, 40-30, deuce, advantage A, game A
        Match match = TennisHarness.runTests("AABBABAA");
        check(match.getState(), BO3_START + "Set: 1-0, game: 0-0");
    }

    private static void testGameWithoutDeuce() {
        Match match = play(new Match(3), "AAB");
        check(match.getState(), BO3_START + "Set: 0-0, game: 30-15");

        play(match, "A");
        check(match.getState(), BO3_START + "Set: 0-0, game: 40-15");

        play(match, "A");
        check(match.getState(), BO3_START + "Set: 1-0, game: 0-0");
    }

    private static void testDeuceAdvantageDeuce() {
        Match match = play(new Match(3), "AAABBB");
        check(match.getState(), BO3_START + "Set: 0-0, game: deuce");

        play(match, "A");
        check(match.getState(), BO3_START + "Set: 0-0, game: advantage A");

        play(match, "B");
        check(match.getState(), BO3_START + "Set: 0-0, game: deuce");
    }

    private static void testDeuceAdvantageGame() {
        Match match = play(new Match(3), "AAABBB B");
        check(match.getState(), BO3_START + "Set: 0-0, game: advantage B");

        play(match, "B");
        check(match.getState(), BO3_START + "Set: 0-1, game: 0-0");
    }

    private static void testSetEndsSixFour() {
        Match match = play(new Match(3), (GAME_A + GAME_B).repeat(4) + GAME_A);
        check(match.getState(), BO3_START + "Set: 5-4, game: 0-0");

        play(match, GAME_A);
        check(match.getSetsScore(), "1-0");
        check(match.getCompletedSetScores(), java.util.List.of("6-4"));
        check(match.getState(), "Best of 3, sets: 1-0, completed sets: [6-4], current Set: 0-0, game: 0-0");
    }

    private static void testSetContinuesAtSixFiveEndsSevenFive() {
        Match match = play(new Match(3), (GAME_A + GAME_B).repeat(5) + GAME_A);
        check(match.getState(), BO3_START + "Set: 6-5, game: 0-0"); // 6-5 is not a win

        play(match, GAME_A);
        check(match.getCompletedSetScores(), java.util.List.of("7-5"));
        check(match.getSetsScore(), "1-0");
    }

    private static void testTiebreakBeginsAtSixSix() {
        Match match = play(new Match(3), (GAME_A + GAME_B).repeat(6));
        check(match.getState(), BO3_START + "Set: 6-6, Tiebreak: 0-0");

        play(match, "A"); // tiebreak points are plain numbers
        check(match.getState(), BO3_START + "Set: 6-6, Tiebreak: 1-0");
    }

    private static void testTiebreakContinuesAtSevenSixEndsEightSix() {
        Match match = play(new Match(3), (GAME_A + GAME_B).repeat(6) + "AB".repeat(6) + "A");
        check(match.getState(), BO3_START + "Set: 6-6, Tiebreak: 7-6"); // 7 points but no 2-point lead

        play(match, "A");
        check(match.getCompletedSetScores(), java.util.List.of("7-6"));
        check(match.getState(), "Best of 3, sets: 1-0, completed sets: [7-6], current Set: 0-0, game: 0-0");
    }

    private static void testBestOfThreeEndsAfterTwoSets() {
        Match match = play(new Match(3), SET_A);
        check(match.isComplete(), false);

        play(match, SET_A);
        check(match.isComplete(), true);
        check(match.getWinner(), Player.A);
        check(match.getState(), "Best of 3, sets: 2-0, completed sets: [6-0, 6-0], winner A");
    }

    private static void testBestOfFiveEndsAfterThreeSets() {
        Match match = play(new Match(5), SET_B + SET_A + SET_B);
        check(match.getSetsScore(), "1-2");
        check(match.isComplete(), false); // 2 sets aren't enough in best of 5

        play(match, SET_B);
        check(match.isComplete(), true);
        check(match.getWinner(), Player.B);
        check(match.getState(), "Best of 5, sets: 1-3, completed sets: [0-6, 6-0, 0-6, 0-6], winner B");
    }

    private static void testPointAfterMatchCompletionRejected() {
        Match match = play(new Match(3), SET_A + SET_A);
        String finalState = match.getState();
        int pointsPlayed = match.getPointsPlayed();

        expectThrows(IllegalStateException.class, () -> play(match, "B"));
        check(match.getState(), finalState);
        check(match.getPointsPlayed(), pointsPlayed);
    }

    private static void testInvalidInputRejected() {
        Match match = new Match(3);
        expectThrows(IllegalArgumentException.class, () -> play(match, "AAC"));
        check(match.getPointsPlayed(), 0); // nothing recorded from a bad sequence
    }

    private static Match play(Match match, String points) {
        return TennisHarness.runTests(match, points, false);
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
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }
}
