/*
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

Data is real-time
 */
public class TelemetryHealthTracker {

    private final Map<String, AssetEventWindow> assetWindows =
            new ConcurrentHashMap<>();

    public void receive(TelemetryEvent event) {

        if (event == null || event.getAssetId() == null) {
            return;
        }

        assetWindows
                .computeIfAbsent(
                        event.getAssetId(),
                        ignored -> new AssetEventWindow())
                .add(event);

    }

    public HealthSnapshot getSnapshot(long now) {

        long windowStart = now - WINDOW_SECONDS;

        Map<String, AssetHealth> assetResults = new LinkedHashMap<>();

        int healthyCount = 0;
        int degradedCount = 0;
        int unknownCount = 0;


        for (Map.Entry<String, AssetEventWindow> entry
                : assetWindows.entrySet()) {

            WindowCalculation calculation =
                    entry.getValue().calculate(windowStart, now);

            String status;
            String reason;

            if (calculation.hasError1001()) {
                status = "degraded";
                reason = "errCode=1001";
                degradedCount++;
            } else if (calculation.getHeartbeatCount()
                    >= EXPECTED_HEARTBEATS - 1) {
                status = "healthy";
                reason = "misses=" + calculateMisses(
                        calculation.getHeartbeatCount());
                healthyCount++;
            } else {
                status = "unknown";
                reason = "misses=" + calculateMisses(
                        calculation.getHeartbeatCount());
                unknownCount++;
            }

            assetResults.put(
                    entry.getKey(),
                    AssetHealth.builder()
                            .status(status)
                            .heartbeats(calculation.getHeartbeatCount())
                            .expectedHeartbeats(EXPECTED_HEARTBEATS)
                            .reason(reason)
                            .build());

        }

        return HealthSnapshot.builder()
                .assets(assetResults)
                .summary(Summary.builder()
                        .healthy(healthyCount)
                        .degraded(degradedCount)
                        .unknown(unknownCount)
                        .build())
                .build();


    }

    private int calculateMisses(int heartbeatCount) {
        return Math.max(0, EXPECTED_HEARTBEATS - heartbeatCount);
    }

    private static class AssetEventWindow {

        private final List<TelemetryEvent> events = new ArrayList<>();

        public synchronized void add(TelemetryEvent event) {
            events.add(event);
        }

        public synchronized WindowCalculation calculate(
                long windowStart,
                long now) {

            // remove past events
            events.removeIf(event -> event.getTs() < windowStart);

            int heartbeatCount = 0;
            boolean hasError1001 = false;

            for (TelemetryEvent event : events) {

                // Future events are stored but not included yet.
                if (event.getTs() > now) {
                    continue;
                }

                if (event.isHeartbeat()) {
                    heartbeatCount++;
                }

                if (event.getErrCode() == DEGRADED_ERROR_CODE) {
                    hasError1001 = true;
                }
            }

            return new WindowCalculation(
                    heartbeatCount,
                    hasError1001);


        }



    }

    @Value
    private static class WindowCalculation {
        int heartbeatCount;
        boolean error1001;

        boolean hasError1001() {
            return error1001;
        }
    }



    @Value
    @Builder
    public static class TelemetryEvent {
        String assetId;
        long ts;
        boolean heartbeat;
        int errCode;
    }

    @Value
    @Builder
    public static class AssetHealth {
        String status;
        int heartbeats;
        int expectedHeartbeats;
        String reason;
    }

    @Value
    @Builder
    public static class Summary {
        int healthy;
        int degraded;
        int unknown;
    }

    @Value
    @Builder
    public static class HealthSnapshot {
        Map<String, AssetHealth> assets;
        Summary summary;
    }




}
