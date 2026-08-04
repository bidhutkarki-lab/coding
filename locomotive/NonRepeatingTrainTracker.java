/*
A freight rail network receives detector events as trains pass through sensors.
Given a stream of train IDs, return the first train that has not been repeated

Time: O(n)
Space: O(n)
*/
public class NonRepeatingTrainTracker {

    public String solution(String[] trainIds) {

        Map<String, Integer> freq = new HashMap<>();

        Queue<String> queue = ArrayDeque<>();

        for(String trainId : trainIds) {
            int count = freq.getOrDefault(trainId, 0) + 1;

            freq.put(trainId, count);

            if(count == 1) {
                queue.add(trainId);
            }

            while(!queue.isEmpty() && freq.get(queue.peek()) > 1) {
                queue.poll();
            }

        }
        return queue.peek(); // returns null when empty
    }



}
