import java.util.*;

/**
Use BFS from beginWord,
try changing each character to find unvisited dictionary words,
and return the number of words in the path when you first reach endWord, or 0 if unreachable.

N = number of dictionary words.
L = length of each word.

Time: O(N × L²)
Space: O(N × L)
**/
public class WordLadder {

    public int ladderLength(String beginWord, String endWord, List<String> wordList) {

        Set<String> unvisited = new HashSet<>(wordList);

        // do not allow when endWord is not in dictonary
        if(!unvisited.contains(endWord)) {
            return 0;
        }

        Queue<String> queue = new ArrayDeque<>();
        queue.add(beginWord);

        int level = 1;
        while(!queue.isEmpty()) {
            int size = queue.size();

            for(int i=0; i<size; i++) {
                String word = queue.poll();
                if(word.equals(endWord)) {
                    return level;
                }

                for(String neighbor : getNeighbors(word, unvisited)) {
                    unvisited.remove(neighbor);
                    queue.add(neighbor);
                }
            }
            level++;
        }
        return 0;
    }

    private List<String> getNeighbors(String word, Set<String> unvisited) {
        List<String> result = new ArrayList<>();

        char[] chars = word.toCharArray();

        for(int i=0; i<word.length(); i++) {

            for(char ch = 'a'; ch <= 'z'; ch++) {
                chars[i] = ch;

                String newWord = new String(chars);
                if(unvisited.contains(newWord)) {
                    result.add(newWord);
                }
            }
            chars = word.toCharArray();
        }
        return result;
    }
}
