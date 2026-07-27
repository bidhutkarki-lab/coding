import java.util.ArrayList;
import java.util.List;

/**
 * Classic 0/1 knapsack.
 *
 * Given items with an integer weight and value, pick a subset whose total
 * weight is at most a capacity while maximizing total value. Each item is taken
 * at most once (0/1). Two solvers are provided:
 *
 *   - bruteForce: enumerate all 2^n subsets. Exact, O(2^n) time, only for small n.
 *   - dynamicProgramming: bottom-up DP, O(n * capacity) time and space.
 *
 * Both return the same maximum value; the DP version also reconstructs one
 * optimal subset.
 */
public class Knapsack {

    public static final class Item {
        final String id;
        final int weight;
        final int value;

        public Item(String id, int weight, int value) {
            if (weight < 0 || value < 0) {
                throw new IllegalArgumentException("weight and value must be non-negative");
            }
            this.id = id;
            this.weight = weight;
            this.value = value;
        }
    }

    public static final class Result {
        final int totalValue;
        final int totalWeight;
        final List<Item> items;

        Result(int totalValue, int totalWeight, List<Item> items) {
            this.totalValue = totalValue;
            this.totalWeight = totalWeight;
            this.items = items;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("value=").append(totalValue)
                    .append(", weight=").append(totalWeight)
                    .append(", items=[");
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(items.get(i).id);
            }
            return sb.append("]").toString();
        }
    }

    /**
     * Exhaustively tries every subset via recursion: at each item we branch on
     * skip vs take. Exponential, but a simple ground truth for validating the
     * DP solver on small inputs.
     */
    public static Result bruteForce(List<Item> items, int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }
        return explore(items, 0, capacity, new ArrayList<>(), 0, 0);
    }

    /**
     * Considers items[index..], given the remaining capacity and the value and
     * weight accumulated so far, and returns the best subset reachable.
     */
    private static Result explore(
            List<Item> items,
            int index,
            int remaining,
            List<Item> chosen,
            int value,
            int weight) {
        if (index == items.size()) {
            return new Result(value, weight, new ArrayList<>(chosen));
        }

        // Branch 1: skip the current item.
        Result best = explore(items, index + 1, remaining, chosen, value, weight);

        // Branch 2: take it, if it fits.
        Item item = items.get(index);
        if (item.weight <= remaining) {
            chosen.add(item);
            Result taken = explore(
                    items,
                    index + 1,
                    remaining - item.weight,
                    chosen,
                    value + item.value,
                    weight + item.weight);
            chosen.remove(chosen.size() - 1); // backtrack
            if (taken.totalValue > best.totalValue) {
                best = taken;
            }
        }
        return best;
    }

    /**
     * Bottom-up DP. dp[i][w] = best value using the first i items with total
     * weight at most w. One optimal subset is recovered by backtracking.
     */
    public static Result dynamicProgramming(List<Item> items, int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }
        int n = items.size();
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            Item item = items.get(i - 1);
            for (int w = 0; w <= capacity; w++) {
                int skip = dp[i - 1][w];
                int take = Integer.MIN_VALUE;
                if (w >= item.weight) {
                    take = dp[i - 1][w - item.weight] + item.value;
                }
                dp[i][w] = Math.max(skip, take);
            }
        }

        List<Item> chosen = new ArrayList<>();
        int totalWeight = 0;
        int w = capacity;
        for (int i = n; i >= 1; i--) {
            if (dp[i][w] != dp[i - 1][w]) { // item i-1 was taken
                Item item = items.get(i - 1);
                chosen.add(item);
                totalWeight += item.weight;
                w -= item.weight;
            }
        }
        return new Result(dp[n][capacity], totalWeight, chosen);
    }

    public static void main(String[] args) {
        List<Item> items = List.of(
                new Item("A", 2, 3),
                new Item("B", 3, 4),
                new Item("C", 4, 5),
                new Item("D", 5, 6));
        int capacity = 5;

        // Optimal: A + B = weight 5, value 7.
        System.out.println("brute force: " + bruteForce(items, capacity));
        System.out.println("dp:          " + dynamicProgramming(items, capacity));

        // Cross-check the two solvers agree on the max value across a few caps.
        for (int cap = 0; cap <= 12; cap++) {
            int bf = bruteForce(items, cap).totalValue;
            int dp = dynamicProgramming(items, cap).totalValue;
            if (bf != dp) {
                throw new AssertionError("mismatch at capacity " + cap + ": " + bf + " vs " + dp);
            }
        }
        System.out.println("brute force and dp agree for capacities 0..12");
    }
}
