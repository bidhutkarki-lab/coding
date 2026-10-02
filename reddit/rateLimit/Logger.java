public class Logger {
    private final Map<String, Integer> lastPrinted = new HashMap<>();

    public boolean shouldPrintMessage(int timestamp, String message) {
        Integer previous = lastPrinted.get(message);

        if(previous != null && timestamp - previous < 10) {
            return false;
        }

        lastPrinted.put(message, timestamp);
        return true;
    }
}
