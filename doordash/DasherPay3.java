import java.util.*;

public class Solution {

    public long calculatePay(List<DeliveryEvent> events, int peakStart, int peakEnd) {

        if(events.isEmpty()) {
            return 0L;
        }

        long totalPay = 0L;
        int activeOrders = 0;
        int waitingOrders = 0;
        int previousTimestamp = events.get(0).getTimestamp();

        for(DeliveryEvent event : events) {
            int currentTimestamp = event.getTimestamp();
            long elapsedMinutes = (long) event.getTimestamp() - previousTimestamp;

            int overlapStart = Math.max(currentTimestamp, peakStart);
            int overlapEnd = Math.min(currentTimestamp, peakEnd);

            long peakMintues = Math.max(OL, (long) overlapEnd - overlapStart);

            int paidOrders = waitingOrders > 0 ? 1 : activeOrders;

            totalPay += (elapsedMinutes + peakMinutes) * paidOrders * 30L;

            switch(event.getStatus()) {
                case ACCEPTED -> activeOrders++;
                case ARRIVED -> waitingOrders++;
                case PICKED_UP -> waitingOrders--;
                case DELIVERED -> activeOrders--;
            }

            previousTimestamp = currentTimestamp;
        }
        return totalPay;
    }

    private static class DeliveryEvent {

        String orderId;
        Integer timestamp;
        String status;
    }

}
