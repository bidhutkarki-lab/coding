public class TieBreakGame {
    private int pointsA;
    private int pointsB;

    public void recordPoint(Player player) {

        if(player == null) {
            throw new IllegalArgumentException("Player is required");
        }

        if(getWinner() != null) {
            throw new IllegalStateException("Tiebreak is already over");
        }

        if(player == Player.A) {
            pointsA++;
        } else {
            pointsB++;
        }


    }

    public Player getWinner() {
        if(pointsA >=7 && pointsA-pointsB >= 2) {
            return Player.A;
        }

        if(pointsB >=7 && pointsB - pointsA >= 2) {
            return Player.B;
        }

        return null;
    }

    public String getScore() {
        return pointsA + "-" + pointsB;
    }
}
