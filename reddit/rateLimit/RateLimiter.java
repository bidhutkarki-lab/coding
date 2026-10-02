import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class RateLimiter {
    private final int maxRequests;
    private final int windowSeconds;
    private final Map<String, Queue<Integer>> requests = new HashMap<>();

    public RateLimiter(int maxRequests, int windowSeconds) {
        if (maxRequests <= 0 || windowSeconds <= 0) {
            throw new IllegalArgumentException("maxRequests and windowSeconds must be positive");
        }

        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }

    public boolean allow(String userId, int now) {
        Queue<Integer> timestamps = requests.computeIfAbsent(userId, key -> new ArrayDeque<>());

        long cutoff = (long) now - windowSeconds;

        // take off the expired requests
        while (!timestamps.isEmpty() && timestamps.peek() <= cutoff) {
            timestamps.poll();
        }

        if (timestamps.size() >= maxRequests) {
            return false;
        }

        timestamps.offer(now);
        return true;
    }

    public static void main(String[] args) {
        RateLimiter limiter = new RateLimiter(3, 10);
        System.out.println(limiter.allow("a", 1));  // true
        System.out.println(limiter.allow("a", 2));  // true
        System.out.println(limiter.allow("a", 3));  // true
        System.out.println(limiter.allow("a", 4));  // false (3 in window)
        System.out.println(limiter.allow("b", 4));  // true (separate user)
        System.out.println(limiter.allow("a", 11)); // true (t=1 expired)
        System.out.println(limiter.allow("a", 12)); // true (t=2 expired)
        System.out.println(limiter.allow("a", 12)); // false
    }
}
