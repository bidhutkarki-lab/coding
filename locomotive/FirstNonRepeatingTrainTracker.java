import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class FirstNonRepeatingTrainTracker {

    private final Map<String, Integer> frequencies = new HashMap<>();

    private final Queue<String> queue = new ArrayDeque<>();

    public void receiveEvent(String trainId) {

        int newCount = frequencies.getOrDefault(trainId, 0) + 1;

        frequencies.put(trainId, newCount);

        // front of the queue is always the first non-repeating train
        if(newCount == 1) {
            queue.offer(trainId);
        }

        // while the front of the queue has count greater than 1, remove it
        while(!queue.isEmpty() && frequencies.get(queue.peek()) > 1) {
            queue.poll();
        }

    }

    public String getFirstNonRepeatingTrain() {
        return queue.isEmpty() ? null : queue.peek();
    }

}

public static void main(String[] args) {

    FirstNonRepeatingTrainTracker tracker =
        new FirstNonRepeatingTrainTracker();

    tracker.receiveEvent("T1");
    System.out.println(tracker.getFirstNonRepeatingTrain()); // T1

    tracker.receiveEvent("T2");
    System.out.println(tracker.getFirstNonRepeatingTrain()); // T1

    tracker.receiveEvent("T1");
    System.out.println(tracker.getFirstNonRepeatingTrain()); // T2

    tracker.receiveEvent("T2");
    System.out.println(tracker.getFirstNonRepeatingTrain()); // null

}
