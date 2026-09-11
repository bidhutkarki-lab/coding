public class MinimumRiderSpeed {

    public static int minimumSpeed(int[] workloads, long hours) {

        int low = 1;
        int high = 0;

        for(int workload : workloads) {
            high = Math.max(high, workload);
        }

        while(low < high) {
            int mid = low + (high - low) / 2;

            if(canFinish(workloads, mid, hours)) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }

        return low;
    }

    public static boolean canFinish(int[] workloads, int speed, long allowedHours) {
        long count = 0;

        for (int workload : workloads) {
            count += Math.ceil((double) workload / speed);

            if(count > allowedHours) {
                return false;
            }
        }

        return true;
    }
}
