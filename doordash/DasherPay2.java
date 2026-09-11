import java.util.*;

public class Solution {

    public long calculatePay(List<DeliveryEvent> events, int peakStart, int peakEnd) {

        if(events.isEmpty()) {
            return 0L;
        }

        long totalPay = 0L;
        int activeOrders = 0;
        int previousTimestamp = events.get(0).getTimestamp();

        for(DeliveryEvent event : events) {
            int currentTimestamp = event.getTimestamp();
            long elapsedMinutes = (long) event.getTimestamp() - previousTimestamp;

            int overlapStart = Math.max(currentTimestamp, peakStart);
            int overlapEnd = Math.min(currentTimestamp, peakEnd);

            long peakMintues = Math.max(OL, (long) overlapEnd - overlapStart);

            totalPay += (elapsedMinutes + peakMinutes) * activeOrders * 30L;

            if(event.getStatus() == Status.ACCEPTED) {
                activeOrders++;
            } else {
                activeOrders--;
            }

            previousTimestamp = event.getTimestamp();

        }
        return totalPay;
    }

    private static class DeliveryEvent {

        String orderId;
        Integer timestamp;
        String status;
    }

}
