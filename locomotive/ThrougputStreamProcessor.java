/*
What if the throughput is stream?
Since I don't have the entire array upfront,
I can't compute every answer immediately. I keep unresolved hours in a monotonic decreasing stack.
When a new throughput arrives, I resolve any previous hours whose throughput is lower than the current one.
If the stream never ends, some hours may never get an answer, so they remain in the stack until either a higher throughput arrives or the stream ends.
Each hour is pushed and popped at most once, so the processing cost is O(1) amortized per event.
*/

import java.util.ArrayDeque;
import java.util.Deque;

public class ThroughputStreamProcessor {

    static class Hour {
        int index;
        int throughput;

        Hour(int index, int throughput) {
            this.index = index;
            this.throughput = throughput;
        }
    }

    private final Deque<Hour> stack = new ArrayDeque<>();
    private int currentHour = 0;

    public void receiveThroughput(int throughput) {

        while (!stack.isEmpty()
                && throughput > stack.peek().throughput) {

            Hour previous = stack.pop();

            System.out.println(
                    "Hour " + previous.index
                    + " waits "
                    + (currentHour - previous.index)
                    + " hour(s)"
            );
        }

        stack.push(new Hour(currentHour, throughput));
        currentHour++;
    }

    // Call this only if the stream ends.
    public void endOfStream() {
        while (!stack.isEmpty()) {
            Hour hour = stack.pop();

            System.out.println(
                    "Hour " + hour.index
                    + " waits 0 hour(s)"
            );
        }
    }

    public static void main(String[] args) {

        ThroughputStreamProcessor processor =
                new ThroughputStreamProcessor();

        processor.receiveThroughput(30);
        processor.receiveThroughput(40);
        processor.receiveThroughput(35);
        processor.receiveThroughput(50);

        processor.endOfStream();
    }
}
