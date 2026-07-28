import java.util.ArrayDeque;
import java.util.Deque;

public class RealTimeTrainValidator {

    private final Deque<String> siding = new ArrayDeque<>();

    public boolean processArrival(String trainId) {
        siding.push(trainId);
        return true;
    }

    public boolean processDeparture(String trainId) {
        if (siding.isEmpty() || !siding.peek().equals(trainId)) {
            return false;
        }

        siding.pop();
        return true;
    }
}

/**

In a production system, I would also add:

- A message queue such as Kafka for incoming detector events
- Ordering by siding ID
- Persistent state so the stack can be recovered after a restart
- Deduplication using an event ID
- Alerts for invalid departures
- A dead-letter queue for malformed or out-of-order events
 */
