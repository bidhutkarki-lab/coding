/*
A freight terminal records the number of containers process each hour.
Given an array of hourly throughput values, determine for each hour how many hours must pass until a higher throughput value occurs.
If no higher throughput occurs later, return 0 for that hour. However, array is cicular

On a original problem, you just scan the array twice
30, 40, 35.. we simulate it as 30,40,35,30,40,35
Time complexity: O(n), worst is 2N
Space complexity: O(n)
*/
import java.util.ArrayDeque;
import java.util.Deque;

public class FreightThroughput {

    public static int[] hoursUntilHigherCircular(int[] throughput) {
        int n = throughput.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < 2 * n; i++) {
            // here is the trick, we use the index % n to get the circular index
            int current = i % n;

            while (!stack.isEmpty()
                    && throughput[current] > throughput[stack.peek()]) {

                int previous = stack.pop();
                result[previous] = i - previous;
            }

            // another change
            if (i < n) {
                stack.push(i);
            }
        }

        return result;
    }
}
