public class TokenBucketRateLimiter {

    private final long capacity;
    private final double refillTokensPerSecond;
    private final Clock clock;

    // Because tryConsume() performs a read-modify-write sequence on shared mutable state
    // In a distributed rate limiter, Redis Lua scripting or another atomic distributed store operation is needed.
    private final ReentrantLock lock = new ReentrantLock();

    private double availableTokens;
    private long lastRefillTimestampMillis;

    public TokenBucketRateLimiter(
            long capacity,
            double refillTokensPerSecond) {

        this(capacity, refillTokensPerSecond, Clock.systemUTC());
    }

    public TokenBucketRateLimiter(
            long capacity,
            double refillTokensPerSecond,
            Clock clock) {

        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }

        if (refillTokensPerSecond <= 0) {
            throw new IllegalArgumentException(
                    "Refill rate must be greater than zero"
            );
        }

        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
        this.clock = clock;

        this.availableTokens = capacity;
        this.lastRefillTimestampMillis = clock.millis();
    }

    public boolean tryConsume() {
        return tryConsume(1);
    }

    public boolean tryConsume(long requestedTokens) {
        if (requestedTokens <= 0) {
            throw new IllegalArgumentException(
                    "Requested tokens must be greater than zero"
            );
        }

        lock.lock();

        try {
            refill();

            if (availableTokens < requestedTokens) {
                return false;
            }

            availableTokens -= requestedTokens;
            return true;
        } finally {
            lock.unlock();
        }
    }

    private void refill() {
        long currentTimestampMillis = clock.millis();

        long elapsedMillis =
                currentTimestampMillis - lastRefillTimestampMillis;

        if (elapsedMillis <= 0) {
            return;
        }

        double tokensToAdd =
                elapsedMillis / 1000.0 * refillTokensPerSecond; // rate * number of seconds passed

        availableTokens = Math.min(
                capacity,
                availableTokens + tokensToAdd
        );

        lastRefillTimestampMillis = currentTimestampMillis;
    }

    public double getAvailableTokens() {
        lock.lock();

        try {
            refill();
            return availableTokens;
        } finally {
            lock.unlock();
        }
    }
}
