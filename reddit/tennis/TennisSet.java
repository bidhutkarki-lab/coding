public class TennisSet {
    private int gamesA;
    private int gamesB;

    private Game currentGame = new Game();
    private TieBreakGame tieBreak;
    private Player winner;

    public void recordPoint(Player player) {
        if(player == null) {
            throw new IllegalArgumentException("Player is required");
        }

        if(winner != null) {
            throw new IllegalStateException("Set is already complete");
        }

        if(tieBreak != null) {
            tieBreak.recordPoint(player);

            Player tieBreakWinner = tieBreak.getWinner();
            if(tieBreakWinner != null) {
                addGameWin(tieBreakWinner);
                winner = tieBreakWinner;
            }
            return;
        }

        currentGame.recordPoint(player);

        Player gameWinner = currentGame.getWinner();
        if(gameWinner == null) {
            return;
        }

        addGameWin(gameWinner);

        // set won and setting the winner
        // player won just won the game must be the set winner, as only their game count increased
        if(Math.max(gamesA, gamesB) >= 6 && Math.abs(gamesA-gamesB) >= 2) {
            winner = gameWinner;
            return;
        }

        if(gamesA == 6 && gamesB == 6) {
            tieBreak = new TieBreakGame();
        } else {
            currentGame = new Game();
        }
    }

    private void addGameWin(Player player) {
        if(player == Player.A) {
            gamesA++;
        } else {
            gamesB++;
        }
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isTieBreakActive() {
        return tieBreak != null && winner == null;
    }

    public String getGamesScore() {
        return gamesA + "-" + gamesB;
    }

    public String getScore() {
        if(winner != null) {
            return "Set:" + getGamesScore() + ", winner " + winner;
        }

        if(tieBreak != null) {
            return "Set: " + getGamesScore() + ", tieBreak: " + tieBreak.getScore();
        }

        return "Set: " + getGamesScore() + ", game: " + currentGame.getScore();
    }




}
