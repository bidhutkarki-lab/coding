public class PokerProblem {

    private static final int HAND_SIZE = 5;

    private enum Category {
        HIGH_CARD(1),
        ONE_PAIR(2),
        THREE_OF_A_KIND(3),
        TWO_PAIR(4),
        FULL_HOUSE(5),
        FOUR_OF_A_KIND(6),
        FIVE_OF_A_KIND(7);

        private final int strength;
    }

    public String[] processNumericPokerOperations(String[][] operations) {

        String[] results = new String[operations.length];

        for (int i = 0; i < operations.length; i++) {

            String[] operation = operations[i];

            switch (operation[0]) {
                case "COMPARE":
                    int comparison = compareHands(
                        operation[1], operation[2]
                    );

                    results[i] = comparison > 0 ? "FIRST"
                        : comparison < 0 ? "SECOND"
                        : "TIE";
                    break;
                case "BEST":
                    results[i] = completeHand(operation[1], true);
                    break;
                case "WORST":
                    results[i] = completeHand(operation[1], false);
                    break;
                default:
                    throw new IllegalArgumentException(
                        "Unknown operation: " + operation[0]
                    );
            }

        }

        return results;

    }


    private Category classify(String hand) {

        int[] frequencies = new int[10];

        for (int i = 0; i < HAND_SIZE; i++) {
            frequencies[hand.charAt(i) - '0']++;
        }

        int largestGroup = 0;
        int pairCount = 0;

        for (int rank = 1; rank <= 9; rank++) {
            largestGroup = Math.max(largestGroup, frequencies[rank]);

            if(frequencies[rank] == 2) {
                pairCount++;
            }
        }

        if (largestGroup == 5) {
            return Category.FIVE_OF_A_KIND;
        }
        if (largestGroup == 4) {
            return Category.FOUR_OF_A_KIND;
        }
        if (largestGroup == 3 && pairCount == 1) {
            return Category.FULL_HOUSE;
        }
        if (pairCount == 2) {
            return Category.TWO_PAIR;
        }
        if (largestGroup == 3) {
            return Category.THREE_OF_A_KIND;
        }
        if (pairCount == 1) {
            return Category.ONE_PAIR;
        }
        return Category.HIGH_CARD;
    }

    // Positive: first wins. Negative: second wins. Zero: tie.
    private int compareHands(String first, String second) {
        int categoryComparision = Integer.compare(classify(first).strength, classify(second).strength);

        if(categoryComparision != 0) {
            return categoryComparision;
        }

        // Preserve the original order; compare from right to left.
        for (int i = HAND_SIZE - 1; i >= 0; i--) {
            int cardComparision = Character.compare(first.charAt(i), second.chartAt(i));

            if(cardComparision != 0) {
                return cardComparision;
            }
        }

        return 0;
    }

    private String completeHand(String partial, boolean findBest) {
        if (partial.length() == HAND_SIZE) {
            return partial;
        }

        char[] hand = new char[HAND_SIZE];
        partial.getChars(0, partial.length(), hand, 0);

        return dfs(hand, partial.length(), findBest);
    }

    private String dfs(char[] hand, int position, boolean findBest) {

        if (position == HAND_SIZE) {
            return new String(hand);
        }

        String selected = null;

        for (char rank = '1'; rank <= '9'; rank++) {
            hand[position] = rank;

            String candidate = dfs(hand, position + 1, findBest);

            if (selected == null) {
                selected = candidate;
                continue;
            }

            int comparision = compareHands(candidate, selected);

            if ((findBest && comparison > 0)
                    || (!findBest && comparison < 0)) {
                selected = candidate;
            }

        }

        return selected;
    }




}
