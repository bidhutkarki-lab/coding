import java.util.*;

/**
The serving rules are:
- Regular games: one player serves the entire game, then the other player serves the next game.
- Tiebreak: the first server serves 1 point, then players alternate every 2 points.
- After a tiebreak: the player who served its first point receives in the next game.
 */
public class ServerRecorder implements MatchListener {

    // Server of the current regular game, or of the first point of the current tiebreak.
    private Player gameServer;

    private int tiebreakPointsPlayed;

    public ServerRecorder(Player server) {
        this.gameServer = server;
    }

    @Override
    public void onGameCompleted() {
        // also covers tiebreaks: the tiebreak's first server receives the next game
        gameServer = gameServer.opponent();
        tiebreakPointsPlayed = 0;
    }

    @Override
    public void onTiebreakPointPlayed() {
        tiebreakPointsPlayed++;
    }

    @Override
    public void onSetCompleted() {
        // serving keeps alternating across sets
    }

    // In a tiebreak the first server serves 1 point, then players alternate every 2 points.
    // Outside a tiebreak tiebreakPointsPlayed is 0, so this returns the game's server.
    public Player getServer() {
        if(tiebreakPointsPlayed == 0) {
            return gameServer;
        }
        return ((tiebreakPointsPlayed - 1) / 2) % 2 == 0 ? gameServer.opponent() : gameServer;
    }
}
