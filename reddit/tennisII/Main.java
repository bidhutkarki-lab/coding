public class Main {
    public static void main(String[] args) {
        Match match = new Match();

        for (Player player : new Player[]{
            Player.A, Player.B,
            Player.A, Player.B,
            Player.A, Player.B
        }) {
            match.pointWonBy(player);
        }

        System.out.println(match.score()); // deuce

        match.pointWonBy(Player.A);
        System.out.println(match.score()); // advantage A

        match.pointWonBy(Player.B);
        System.out.println(match.score()); // deuce

        match.pointWonBy(Player.B);
        System.out.println(match.score()); // advantage B

        match.pointWonBy(Player.B);
        System.out.println(match.score()); // winner B
    }
}
