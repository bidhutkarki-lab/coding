/**
 * Use BFS.
 * Compare the current word against unvisited dictionary words.
 * If a word differs in exactly 1 or 2 positions, set the current word as its parent, mark it visited, and enqueue it.
 * For each current word, also check whether it can reach endWord, which may be outside the dictionary.
 * Once reachable, follow the parent map backward and reverse it to return one shortest path.
 * Return [] if unreachable.
 *
 * Time: O(N²L). Space: O(N).
 * Up to N words processed, each scanning N candidates and comparing L characters
 * while: processes up to N words from the queue.
 * iterator: scans up to N remaining words for each processed word.
 * canTransform(): compares up to L characters per pair.
 */
public class WordLadderIII {

    public List<String> shortestTransformation(String beginWord, String endWord, List<String> wordList) {
        if(beginWord.equals(endWord)) {
            return List.of(beginWord);
        }

        Set<String> remaining = new HashSet<>(wordList);
        remaining.remove(beginWord);
        remaining.remove(endWord);

        Queue<String> queue = new ArrayDeque<>();
        Map<String, String> parent = new HashMap<>();
        queue.add(beginWord);

        while(!queue.isEmpty()) {
            String word = queue.poll();

            if(differsByTwo(word, endWord)) {
                parent.put(endWord, word);
                return buildPath(endWord, parent);
            }

            Iterator<String> iterator = remaning.iterator();

            while(iterator.hasNext()) {
                String candidate = iterator.next();

                if(differsByTwo(word, candidate)) {
                    parent.put(candidate, word);
                    queue.add(candidate);
                    iterator.remove(); // mark visited when enqueued
                }
            }
        }
        return Collections.emptyList();
    }

    private boolean differsByTwo(String from, String to) {
        int differences = 0;

        for(int i=0; i<from.length(); i++) {
            if(from.charAt(i) != to.charAt(i)) {
                differences++;

                if(differences > 2) {
                    return false;
                }
            }
        }

        return differences == 1 || differences == 2;
    }

    public List<String> buildPath(String endWord, Map<String, String> parent) {
        List<String> path = new LinkedList<>();

        for(String word = endWord; word != null; word = parent.get(word)) {
            path.addFirst(word);
        }


        return path;
    }


}
