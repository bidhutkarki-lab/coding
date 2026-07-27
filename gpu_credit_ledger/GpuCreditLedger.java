import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Time-aware GPU credit ledger.
 *
 * The model is a timeline, not the input stream: grants are active for
 * [start, expire), subtractions permanently consume active credits (earliest
 * expiry first) and that consumption persists forward until the grant expires.
 * Because timestamps may be unsorted, subtractions are applied in
 * (timestamp, input-order) order so an earlier-in-time subtraction always sees
 * grants before a later one does. BALANCE t reports the credits still active at
 * t, i.e. accounting for every subtraction whose timestamp is <= t.
 */
public class GpuCreditLedger {

    private static final class Grant {
        final long amount;
        final long start;
        final long expire;
        // (timestamp, amount) pairs recording when credits were consumed.
        final List<long[]> consumptions = new ArrayList<>();
        long consumedTotal = 0; // running total while applying subtractions

        Grant(long amount, long start, long expire) {
            this.amount = amount;
            this.start = start;
            this.expire = expire;
        }

        boolean activeAt(long t) {
            return start <= t && t < expire;
        }
    }

    private static final class Subtract {
        final long amount;
        final long timestamp;
        final int inputIndex;

        Subtract(long amount, long timestamp, int inputIndex) {
            this.amount = amount;
            this.timestamp = timestamp;
            this.inputIndex = inputIndex;
        }
    }

    private final List<Grant> grants = new ArrayList<>();
    private final List<Subtract> subtracts = new ArrayList<>();
    private int opCounter = 0;

    public void add(long amount, long start, long expire) {
        grants.add(new Grant(amount, start, expire));
        opCounter++;
    }

    public void subtract(long amount, long timestamp) {
        subtracts.add(new Subtract(amount, timestamp, opCounter++));
    }

    /**
     * Applies all recorded subtractions in timeline order, then returns the
     * active balance at t. Safe to call multiple times (idempotent).
     */
    public long balance(long timestamp) {
        applySubtractions();
        long total = 0;
        for (Grant g : grants) {
            if (!g.activeAt(timestamp)) {
                continue;
            }
            long consumedUpToT = 0;
            for (long[] c : g.consumptions) {
                if (c[0] <= timestamp) {
                    consumedUpToT += c[1];
                }
            }
            total += g.amount - consumedUpToT;
        }
        return total;
    }

    private boolean applied = false;

    private void applySubtractions() {
        if (applied) {
            return;
        }
        applied = true;

        subtracts.sort(
                Comparator.comparingLong((Subtract s) -> s.timestamp)
                        .thenComparingInt(s -> s.inputIndex));

        for (Subtract s : subtracts) {
            List<Grant> active = new ArrayList<>();
            long available = 0;
            for (Grant g : grants) {
                if (g.activeAt(s.timestamp)) {
                    active.add(g);
                    available += g.amount - g.consumedTotal;
                }
            }

            // All-or-nothing: if the active balance cannot cover the request,
            // consume nothing.
            if (available < s.amount) {
                continue;
            }

            // Consume from grants that expire earliest first.
            active.sort(
                    Comparator.comparingLong((Grant g) -> g.expire)
                            .thenComparingLong(g -> g.start));

            long need = s.amount;
            for (Grant g : active) {
                if (need == 0) {
                    break;
                }
                long remaining = g.amount - g.consumedTotal;
                long take = Math.min(remaining, need);
                if (take > 0) {
                    g.consumedTotal += take;
                    g.consumptions.add(new long[] {s.timestamp, take});
                    need -= take;
                }
            }
        }
    }

    /**
     * Convenience driver: runs a list of textual operations and returns the
     * output of every BALANCE op, in input order.
     *   ADD grantId amount start expire
     *   SUBTRACT amount timestamp
     *   BALANCE timestamp
     */
    public static List<Long> run(List<String> operations) {
        GpuCreditLedger ledger = new GpuCreditLedger();
        List<Long> balanceOps = new ArrayList<>(); // timestamps, in input order

        for (String op : operations) {
            String[] parts = op.trim().split("\\s+");
            switch (parts[0]) {
                case "ADD":
                    // parts[1] = grantId (unused for computation)
                    ledger.add(
                            Long.parseLong(parts[2]),
                            Long.parseLong(parts[3]),
                            Long.parseLong(parts[4]));
                    break;
                case "SUBTRACT":
                    ledger.subtract(
                            Long.parseLong(parts[1]),
                            Long.parseLong(parts[2]));
                    break;
                case "BALANCE":
                    balanceOps.add(Long.parseLong(parts[1]));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown op: " + parts[0]);
            }
        }

        List<Long> output = new ArrayList<>();
        for (long t : balanceOps) {
            output.add(ledger.balance(t));
        }
        return output;
    }

    public static void main(String[] args) {
        List<Long> out = run(List.of(
                "ADD A 100 0 10",   // active [0,10)
                "ADD B 50 5 20",    // active [5,20)
                "SUBTRACT 30 5",    // consume 30 from A (expires first)
                "BALANCE 5",        // A:70 + B:50 = 120
                "BALANCE 12",       // A expired, B:50 = 50
                "SUBTRACT 60 6",    // consume 60 more from A
                "BALANCE 6",        // A:10 + B:50 = 60
                "BALANCE 11",       // A expired, B:50 = 50
                "SUBTRACT 1000 5",  // insufficient -> no-op
                "BALANCE 5"));      // unchanged: 120

        System.out.println(out); // [120, 50, 60, 50, 120]
    }
}
