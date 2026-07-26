public class RateLimiterDemo {

    public static void main(String[] args) throws InterruptedException {
        TokenBucketRateLimiter rateLimiter =
                new TokenBucketRateLimiter(
                        10, // capacity
                        2 // refill rate per second
                );

        for (int i = 1; i <= 15; i++) {
            boolean allowed = rateLimiter.tryConsume();

            System.out.println(
                    "Request " + i + ": "
                            + (allowed ? "ALLOWED" : "REJECTED")
            );
        }

        Thread.sleep(2_000);

        System.out.println("After waiting:");

        for (int i = 1; i <= 5; i++) {
            boolean allowed = rateLimiter.tryConsume();

            System.out.println(
                    "Request " + i + ": "
                            + (allowed ? "ALLOWED" : "REJECTED")
            );
        }
    }
}
