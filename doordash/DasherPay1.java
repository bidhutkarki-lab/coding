import java.util.*;

public class Solution {

    public long calculatePay(List<DeliveryEvent> events) {

        if(events.isEmpty()) {
            return 0L;
        }

        long totalPay = 0L;
        int activeOrders = 0;
        int previousTimestamp = events.get(0).getTimestamp();

        for(DeliveryEvent event : events) {

            long elapsedMinutes = (long) event.getTimestamp() - previousTimestamp;

            totalPay += elapsedMinutes * activeOrders * 30L;

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
