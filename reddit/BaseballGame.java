/**
 * Leetcode 682
 *
 * Solve using stack. Easy problem. You can use java.math.BigInteger too.
 * Time: O(n)
 * Space: O(n)
 */
public class BaseballGame {
    public int calPoints(String[] operations) {

        Stack<Integer> stack = new Stack<>();

        for(int i=0; i<operations.length; i++) {
            String operation = operations[i];

            switch(operation) {
                case "C":
                    stack.pop();
                    break;
                case "D":
                    stack.push(stack.peek() * 2);
                    break;
                case "+":
                    int a = stack.pop();
                    int b = stack.peek();
                    stack.push(a);
                    stack.push(a + b);
                    break;
                default:
                    stack.push(Integer.valueOf(operation));

            }
        }

        int total = 0;
        for(int score: stack) {
            total = total + score;
        }

        return total;
    }
}
