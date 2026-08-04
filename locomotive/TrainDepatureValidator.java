/**
 *
 * A rail yard receives trains in a specific arrival order.
 * Trains can be temporarily held in a siding track that behaves like a stack (Last In, First Out).
 * Given an arrival sequence and departure sequence, determine whether the departure sequence is possible."
 *
 * Arrival:   T1 T2 T3
 * Departure: T2 T3 T1
 * Valid case
 *
 * Similar to LeetCode Problem 946
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */

public class TrainDepatureValidator {

    public static boolean isDepaturePossible(List<String> arrival, List<String> depature) {

        if(arrival.size() != depature.size()) {
            return false;
        }

        Stack<String> stack = new Stack<>();

        int departureIndex = 0;

        for(String train : arrival) {
            stack.push(train);

            while(!stack.isEmpty() &&
            depatureIndex < departure.size() &&
            stack.peek().equals(departure.get(departureIndex))) {
                stack.pop();
                departureIndex++;
            }
        }

        return departureIndex == depature.size();
    }
}

/**
 * Follow up: What if there are multiple sidings?
 *
 * With multiple sidings, each siding is its own stack.
 *
 * For every arriving train, we can choose which siding to place it into. A train can depart only when it is on top of oen of the sidings.
 *
 * - try placing each arriving train into one of the available stacks
 * - whenever the next expected departing train is on top of any stack, pop it
 * - if no placement can eventually produce the full departure order, return false
 */
