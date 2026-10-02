import java.util.*;

public final class MatchListenerFactory {

    private final static ServerRecorder serverRecorder = new ServerRecorder(Player.A);

    private final static SideRecorder sideRecorder = new SideRecorder(Side.NEAR);

    private MatchListenerFactory() {

    }

    // Every match listener is registered here.
    public static List<MatchListener> getListeners() {
        return List.of(serverRecorder, sideRecorder);
    }

    public static ServerRecorder getServerRecorder() {
        return serverRecorder;
    }

    public static SideRecorder getSideRecorder() {
        return sideRecorder;
    }

}
