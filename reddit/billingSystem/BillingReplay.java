import java.util.*;

public class BillingReplay {

    record Transaction(
        int userId,
        long timestamp,
        Map<String, Long> monetaryValues,
        boolean overwrite
        boolean undoLast,
        boolean redoLast
    ) {}

    record Change(
        Map<String, Long> before,
        Map<String, Long> after
    ) {}

    static class BillingStatus {
        private final Map<String, Long> amounts = new HashMap<>();

        private final Stack<Change> undoStack = new Stack<>();
        private final Stack<Change> redoStack = new Stack<>();

        BillingStatus(List<String> monetaryColumns) {
            for (String column : monetaryColumns) {
                amounts.put(column, 0L);
            }
        }

        void ingest(String transactionId, Transaction transaction) {
            if(transaction.undoLast()) {
                undo();
                return;
            }

            if(transaction.redoLast()) {
                redo();
                return;
            }

            Map<String, Long> before = new HashMap<>();
            Map<String, Long> after = new HashMap<>();


            for (String column : amounts.keySet()) {
                if (!transaction.monetaryValues().containsKey(column)) {
                    continue;
                }

                long current = amounts.get(column);
                long value = transaction.monetaryValues().get(column);

                long updated = transaction.overwrite() ? value : current + value;

                before.put(column, current);
                after.put(column, updated);
            }

            // Prevent empty transaction from entering undo history or clearing redo history
            if(before.isEmpty()) {
                return;
            }

            amounts.putAll(after);
            undoStack.push(new Change(before, after));
            redostack.clear();
        }

        private void undo() {
            if(undoStack.isEmpty()) {
                return;
            }

            Change change = undoStack.pop();
            amounts.putAll(change.before());
            redoStack.push(change);
        }

        private void redo() {
            if(redoStack.isEmpty()) {
                return;
            }

            Change change = redoStack.pop();
            amounts.putAll(change.after());
            undoStack.push(change);
        }

        Map<String, Long> getAmounts() {
            return new HashMap<>(amounts);
        }
    }

    public static Map<Integer, BillingStatus> rebuildBillingStatuses(
        Map<String, Transaction> transactions,
        List<String> monetaryColumns
    ) {
        List<Map.Entry<String, Transaction>> ordered = new ArrayList<>(transactions.entrySet());

        ordered.sort(
            Comparator
                .comparingLong((Map.Entry<String, Transaction> entry) -> entry.getValue().timestamp())
                .thenComparing(Map.Entry::getKey)
        );

        Map<Integer, BillingStatus> statuses = new HashMap<>();

        for (Map.Entry<String, Transaction> entry : ordered) {
            Transaction transaction = entry.getValue();

            BillingStatus status = statuses.computeIfAbsent(
                transaction.userId(),
                userId -> new BillingStatus(monetaryColumns)
            );

            status.ingest(entry.getKey(), transaction);
        }

        return statuses;
    }
}
