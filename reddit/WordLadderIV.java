
/**
 * Validate Word Ladder Problem
 * Go through the words in order.
 * Use a HashSet to track words already seen; if a word repeats, return false.
 * Compare each word with the previous word: their lengths must match, and exactly one character position must differ.
 * If every word passes, return true.
 *
 * Check two things: no word repeats, and each word changes exactly one letter from the word before it.
 * If both are true, the sequence is valid.
 *
 * Time: O(N × L) · Space: O(N)
 * **/
public class WordLadderIV {

    public boolean isValidSequence(List<String> words) {
        if(words.isEmpty()) {
            return false;
        }

        Set<String> seen = HashSet<>();

        for(int i=0; i<words.size(); i++) {
            String current = words.get(i);

            if(!seen.add(current)) {
                return false;
            }

            if(i > 0 && !differsByOne(words.get(i-1), current)) {
                return false;
            }
        }
        return true;
    }

    private boolean differsByOne(String first, String second) {
        if(first.length() != second.length()) {
            return false;
        }

        int differences = 0;
        if(int i=0; i<first.length(); i++) {
            if(first.charAt(i) != second.charAt(i)) {
                differences++;
            }

            if(differences > 1) {
                return false;
            }
        }

        return differences == 1;
    }
}
