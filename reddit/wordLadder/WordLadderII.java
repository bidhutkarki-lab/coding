import java.util.*;

/**
 * Given begin
    - Each step replaces characters; no insertion or deletion.
    - All intermediate words must be in the dictionary.
    - begin and end do not need to be in the dictionary.
    - Use words with the same length as begin.
    Part 1: Change exactly one character per step. Return whether a path exists.
    Part 2: Allow one or two character changes per step. Return whether a path exists.
    Part 3: Using Part 2’s rules, return a shortest path, including both endpoints, or an empty list if none exists.
 */
public class WordLadderII {

    public boolean hasPathOneEdit(
        String begin,
        String end,
        Set<String> words
    ) {

        if(begin.length() != end.length()) {
            return false;
        }

        // Allow a zero-step path
        if(begin.equals(end)) {
            return true;
        }

        Set<String> unvisited = new HashSet<>();
        for(String word : words) {
            if(word.length() == begin.length()) {
                unvisited.add(word);
            }
        }

        unvisited.add(end);
        unvisited.remove(begin);

        Queue<String> queue = new ArrayDeque<>();
        queue.add(begin);

        while(!queue.isEmpty()) {
            String word = queue.poll();

            if(word.equals(end)) {
                return true;
            }

            for(String neighbor : getNeighbors(word, unvisited)) {
                unvisited.remove(neighbor);
                queue.add(neighbor);
            }
        }

        return false;

    }

    private List<String> getNeighbors(
            String word,
            Set<String> unvisited) {

        List<String> result = new ArrayList<>();
        char[] chars = word.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char original = chars[i];

            for(char ch = 'a'; ch <='z'; ch++) {
                chars[i] = ch;

                String newWord = new String(chars);
                if(unvisited.contains(newWord)) {
                    result.add(newWord);
                }
            }

            chars[i] = original;
        }

        return result;
    }
}
