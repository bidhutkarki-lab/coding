/**
 * TennisGame manages points. TennisSet manages games. Sides are separate from scoring.
 *
 * Store two point counts. A player wins when they have at least 4 points and lead by 2.
 * Whenever both counts are equal and at least 3, reset them to 3-3.
 */
public class TennisGameI {
    private final String player1;
    private final String player2;

    private int points1;
    private int points2;

    public TennisGameI(String player1, String player2) {
        if(player1 == null || player2 == null || player1.isEmpty() || player2.isEmpty() || player1.equals(player2)) {
            throw new IllegalArgumentException("Player names must be nonempty and distinct");
        }

        this.player1 = player1;
        this.player2 = player2;
    }

    public void addScore(String player) {
        if(!player1.equals(player) && !player2.equals(player)) {
            throw new IllegalArgumentException("Invalid player: " + player);
        }

        if(!getResult().isEmpty()) {
            throw new IllegalAgurmentException("Game is already over");
        }

        if(player1.equals(player)) {
            points1++;
        } else {
            points2++;
        }

        // Returning from advantage to deuce resets the raw score
        if(points1 == points2 && points1 >= 3) {
            points1 = 3;
            points2 = 3;
        }
    }

    public int[] getScore() {
        return new int[] {points1, points2};
    }

    public String getResult() {
        if(points1 >= 4 && points1-points2 >= 2) {
            return player1;
        }

        if(points2 >= 4 && points2-points1 >= 2) {
            return player2;
        }

        return "";
    }

    public String getHumanScore() {
        String winner = getResult();

        if(!winner.isEmpty()) {
            return "Game " + winner;
        }

        if(points1 >= 3 && points2 >=3) {
            if(points1 == points2) {
                retur "Deuce";
            }

            return "Advantage " + (points1 > points2 ? player1 : player2);
        }

        return pointName(points1) + "-" + pointName(points2);
    }

    private String pointName(int points) {
        switch(points) {
            case 0: return "Love";
            case 1: return "15";
            case 2: return "30";
            case 3: return "40";
            default:
                throw new IllegalStateException("Invalid point count");
        }
    }
}

public class TennisSet {
    private final String player1;
    private final String player2;
    private final int gamesToWin;

    private int games1;
    private int games2;
    private TennisGame currentGame;

    public TennisSet(String player1, String player2) {
        this(player1, player2, 3);
    }

    public TennisSet(String player1, String player2, int gamesToWin) {
        if(gamesToWin < 1) {
            throw new IllegalArgumentException(
                "gamesToWin must be at least 1"
            );
        }

        this.currentGame = new TennisGame(player1, palyer2);
        this.player1 = player1;
        this.player2 = player2;
        this.gamesToWin = gamesToWin;
    }

    public void addScore(String player) {
        if(!player1.equals(player) && !player2.equals(player)) {
            throw new IllegalArgumentException("Invalid player: " + player);
        }

        if(!getSetWinner().isEmpty()) {
            throw new IllegalStateException("Set is already over");
        }

        currentGame.addScore(player);

        String gameWinner = currentGame.getResult();

        if(gameWinner.isEmpty()) {
            return;
        }

        if(player1.equals(gameWinner)) {
            games1++;
        } else {
            games2++;
        }

        if(getSetWinner().isEmpty()) {
            currentGame = new TennisGame(player1, player2);
        }
    }

    public int[] getSetScore() {
        return new int[] {games1, games2};
    }

    public String getSetWinner() {
        if(games1 >= gamesToWin) {
            return player1;
        }

        if(games2 >= gamesToWin) {
            return player2;
        }

        return "";
    }

    // Expose queries without exposing the mutable game itself
    public int[] getGameScore() {
        return currentGame.getScore();
    }

    public String getGameHumanScore() {
        return currentGame.getHumanScore();
    }

    public String getGameResult() {
        return currentGame.getResult();
    }

    public Map<String, String> getSides() {
        long completedGames = (long) games1 + games2;
        long switches = (completedGames + 1) / 2;

        boolean swapped = switches % 2 == 1;

        Map<String, String> sides = new LinkedHashMap<>();

        sides.put(player1, swapped ? "far" : "near");
        sides.put(player2, swapper ? "near" : "far");

        return sides;
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        PrintWriter out = new PrintWriter(
            new BufferedWriter(new OutputStreamWriter(System.out))
        );

        StringTokenizer header = new StringTokenizer(reader.readLine());

        String player1 = header.nextToken();
        String player2 = header.nextToken();
        int gamesToWin = Integer.parseInt(header.nextToken());

        TennisSet set = new TennisSet(player1, player2, gamesToWin);
        int q = Integer.parseInt(reader.readLine().trim());

        for(int i=0; i<q; i++) {
            StringTokenizer tokens = new StringTokenizer(reader.readLine());

            String command = tokens.nextToken();

            try {
                switch(command) {
                    case "POINT":
                        set.addScore(tokens.nextToken());
                        break;
                    case "GAME_SCORE":
                        int[] score = set.getGameScore();
                        System.out.println(score[0] + "-" + score[1]);
                        break;
                    case "GAME_HUMAN":
                        System.out.println(set.getGameHumanScore());
                        break;
                    case "GAME_RESULT":
                        System.out.println(set.getGameResult());
                        break;
                    case "SET_SCORE": {
                        int[] score = set.getSetScore();
                        System.out.println(score[0] + "-" + score[1]);
                        break;
                    }

                    case "SET_WINNER":
                        System.out.println(set.getSetWinner());
                        break;

                    case "SIDES": {
                        Map<String, String> sides = set.getSides();

                        out.println(
                            player1 + ":" + sides.get(player1) + " "
                            + player2 + ":" + sides.get(player2)
                        );
                        break;
                    }

                    default:
                        throw new IllegalArgumentException(
                            "Unknown command: " + command
                        );
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            out.println("ERROR: " + e.getMessage());
        }
    }
}
