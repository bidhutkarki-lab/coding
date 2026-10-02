import java.util.*;

class LogLimiterOptimized {
    private static final int WINDOW = 10;

    private record Entry(int timestamp, String message) {}

    private final Map<String, Integer> lastPrinted = new HashMap<>();
    private final Queue<Entry> accepted = new ArrayDeque<>();

    public synchronized boolean shouldPrintMessage(int timestamp, String message) {
        // remove from map and queue if expired
        while (!accepted.isEmpty() && accepted.peek().timestamp() <= timestamp - WINDOW) {
            lastPrinted.remove(accepted.poll().message());
        }

        // if it in in map still, rate is limited here
        if (lastPrinted.containsKey(message)) {
            return false;
        }

        // add to map and queue
        lastPrinted.put(message, timestamp);
        accepted.offer(new Entry(timestamp, message));
        return true;
    }
}
