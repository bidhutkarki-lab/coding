import java.util.*;

public class MeetingRoomII {

    public static int minMeetingRooms(int[][] intervals) {

        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        Queue<Integer> endTimes = new PriorityQueue<Integer>();

        for(int[] interval : intervals) {

            if(!endTimes.isEmpty() && endTimes.peek() <= interval[1]) {
                // if the earliest-ending meeting finishes before a new meetings starts,
                // reuse that room
                endTimes.poll();

            }

            endTimes.offer(interval[1]);
        }

        return endTimes.size();

    }


    public static void main(String[] args) {
        int[][] intervals = {
            {0, 30},
            {5, 10},
            {15, 20}
        };

        System.out.println(minMeetingRooms(intervals)); // 2
    }
}
