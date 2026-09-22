public class Game {

    private static final String[] LABELS = {"0", "15", "30", "40"};

    private int pointsA;
    private int pointsB;

    public void recordPoint(Player player) {
        if(player == null) {
            throw new IllegalArgumentException("Player is required");
        }

        if(getWinner() != null) {
            throw new IllegalStateException("Game is already over");
        }

        if(player == Player.A) {
            pointsA++;
        } else {
            pointsB++;
        }
    }

    public Player getWinner() {
        if(pointsA >=4 && pointsA - pointsB >= 2) {
            return Player.A;
        }

        if(pointsB >= 4 && pointsB - pointsA >= 2) {
            return Player.B;
        }

        return null;
    }

    public String getScore() {
        Player winner = getWinner();

        if(winner != null) {
            return "winner " + winner;
        }

        if(pointsA >= 3 && pointsB >= 3) {
            if(pointsA == pointsB) {
                return "deuce";
            }
            return "advantage " + (pointsA > pointsB ? Player.A : Player.B);
        }

        return LABELS[pointsA] + "-" + LABELS[pointsB];
    }

}
