import java.util.ArrayDeque;
import java.util.Deque;

/*
A freight terminal records the number of containers process each hour.
Given an array of hourly throughput values, determine for each hour how many hours must pass until a higher throughput value occurs.
If no higher throughput occurs later, return 0 for that hour.

Monotonic stack like a daily max temperature problem
Time complexity: O(n), worst is 2N
Space complexity: O(n)
*/
public class FreightThroughput {

    public static int[] hoursUntilHigherThroughput(int[] throughput) {
        int[] result = new int[throughput.length];
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < throughput.length; i++) {
            while (!stack.isEmpty()
                    && throughput[i] > throughput[stack.peek()]) {

                int prev = stack.pop();
                result[prev] = i - prev;
            }

            stack.push(i);
        }

        return result;
    }
}

/*
Follow up:
how to do find the next smaller throughput ?
- pop when the current value is smaller than the value at the stored index

Return the throughput value instead of the distance.
- result[prev] = throughput[i];

What if the throughput is stream?
Since I don't have the entire array upfront,
I can't compute every answer immediately. I keep unresolved hours in a monotonic decreasing stack.
When a new throughput arrives, I resolve any previous hours whose throughput is lower than the current one.
If the stream never ends, some hours may never get an answer, so they remain in the stack until either a higher throughput arrives or the stream ends.
Each hour is pushed and popped at most once, so the processing cost is O(1) amortized per event.

Check ThroughputStreamProcessor.java for code

*
