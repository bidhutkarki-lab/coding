/**
 * Use two stacks
 * Undo stack
 * Redo stack
 */
public class BillingSystem {
    public long[] billingStatusLog(String[] operations, long[] amounts) {
        Stack<Long> undo = new Stack<>();
        Stack<Long> redo = new Stack<>();

        long current = 0;
        long[] result = new long[operations.length];

        for(int i=0; i<operations.length; i++) {
            switch(operations[i]) {
                case "ADD":
                    undo.push(current);
                    redo.clear();
                    current += amounts[i];
                    break;

                case "SET":
                    undo.push(current);
                    redo.clear();
                    current = amounts[i];
                    break;
                case "UNDO":
                    if(!undo.isEmpty()) {
                        redo.push(current);
                        current = undo.pop();
                    }
                    break;
                case "REDO":
                    if(!redo.isEmpty()) {
                        undo.push(current);
                        current = redo.pop();
                    }
                    break;
            }

            result[i] = current;
        }

        return result;

    }
}
