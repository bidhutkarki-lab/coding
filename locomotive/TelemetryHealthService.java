/**
 Build a Java function/API that checks telemetry from the last 60 seconds and determines each asset’s health.

Each asset should send 12 heartbeats per minute:

Degraded: Any event contains error code 1001.
Healthy: At least 11 of 12 heartbeats are received.
Unknown: Fewer than 11 heartbeats are received.

Return each asset’s status, heartbeat count, reason, and an overall summary.

For the sample:

LOCO-1973: healthy
CRANE-A12: degraded
HST-7: unknown

healthy=1, degraded=1, unknown=1
 */
public class TelemetryHealthService {


    private static final int WINDOW_SECONDS = 60;
    private static final int DEGRADED_ERROR_CODE = 1001

    public HealthSnapshot getHealthSnapshot(
        List<TelemetryEvent> events,
        long now,
        int heartbeatIntervalSeconds
    ) {
        if(heartbeatIntervalSeconds <= 0  || WINDOW_SECONDS % heartbeatIntervalSeconds != 0) {
            throw new IllegalArgumentException("Heartbeat interval must divide evenly into 60 seconds");
        }

        int expectedHeartbeats = WINDOW_SECONDS / heartbeatIntervalSeconds;
        long windowStart = now - WINDOW_SECONDS;

        Map<String, AssetAccumulator> assets = new LinkedHashMap<>();

        if(events != null) {
            for(TelemetryEvent event : events) {
                if(event == null || event.getAssetId() == null || event.getTs() < windowStart || event.getTs() > now) {
                    continue;
                }


                if(event.isHeartbeat()) {
                    accumulator.incrementHeartbeats();
                }

                if(event.getErrCode() == DEGRADED_ERROR_CODE) {
                    accumulator.markDegraded();
                }
            }
        }

        Map<String, AssetHealth> assetHealth = new LinkedHashMap<>();

        int healthyCount = 0;
        int degradedCount = 0;
        int unknownCount = 0;

        for(Map.Entry<String, AssetAccumulator> entry : assets.entrySet()) {
            AssetAccumulator accumulator = entry.getValue();

            String status;
            String reason;

            if(accumulator.hasDegradedError()) {
                status = "degraded";
                reason = "errCode=1001";
                degradedCount++;
            } elese if(accumulator.getHeartbeats() >= expectedHeartbeats -1) {
                status = "healthy";
                reason = "misses=" + Math.max(0, expectedHeartbeats - accumulator.getHeartbeats());
                healthyCount++;
            } else {
                status = "unknown";
                reason = "misses=" + Math.max(0, expectedHeartbeats - accumulator.getHeartbeats());
                unknownCount++;
            }

            assetHealth.put(entry.getKey(), AssetHealth.builder()
            .status(status)
            .heartbeats(accumulator.getHeartbeats())
            .expectedHeartbeats(expectedHeartbeats)
            .reason(reason)
            .build());
        }

        return HealthSnapshot.builder()
        .assets(assetHealth)
        .summary(Summary.builder()
            .healthy(healthyCount)
            .degraded(degradedCount)
            .unknown(unknownCount)
            .build()
        )
        .build();
    }

    @Getter
    private static class AssetAccumulator {
        private int heartbeats;
        private boolean degradedError;

        void incrementHeartbeats() {
            heartbeats++;
        }

        void makDegraded() {
            degradedError = true;
        }

        boolean hasDegradedError() {
            return degradedError;
        }
    }

    @Builder
    public static class TelemetryEvent {
        String assetId;
        long ts;
        boolean heartbeat;
        int errCode;
    }

    @Builder
    public static class AssetHealth {
        String status;
        int heartbeats;
        int expectedHeartbeats;
        String reason;
    }

    @Builder
    public static class Summary {
        int healthy;
        int degraded;
        int unknown;
    }

    @Builder
    public static class HealthSnapshot {
        Map<String, AssetHealth> assets;
        Summary summary;
    }
}

/**
 * what if the data is realtime:
    *
    * Read telemetry from Kafka or Kinesis.
    * Use the event’s ts as the event timestamp.
    * Group events by assetId.
    * Keep a 60-second window.
    * Recalculate every 5 seconds.
    * Count heartbeat=true events.
    * Check whether error 1001 occurred.
    * Write the latest result to Redis or DynamoDB.
    * Spring Boot reads that result for the snapshot API.
 */
