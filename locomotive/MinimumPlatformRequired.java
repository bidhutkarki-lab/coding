import java.util.Arrays;
import java.util.PriorityQueue;

/*
Given train arrival and departure times, find the minimum number of platforms needed so no train has to wait.

A platform is occupied from arrival until departure.
If one train departs exactly when another arrives, they can reuse the same platform.

Time: O(nlogn)
Space: O(n)
*/
public class MinimumPlatformRequired {

    public int minimumPlatforms(int[][] schedule) {
        if (schedule == null || schedule.length == 0) {
            return 0;
        }

        Arrays.sort(schedule, (a, b) -> Integer.compare(a[0], b[0]));

        PriorityQueue<Integer> departureTimes = new PriorityQueue<>();

        for (int[] train : schedule) {
            if (!departureTimes.isEmpty()
                    && departureTimes.peek() <= train[0]) {
                departureTimes.poll();
            }

            departureTimes.offer(train[1]);
        }

        return departureTimes.size();
    }
}
