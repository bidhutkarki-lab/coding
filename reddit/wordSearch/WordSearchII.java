class Solution {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word;
    }

    public List<String> findWords(char[][] board, String[] words) {

        TrieNode root = new TrieNode();

        for(String word : words) {
            TrieNode node = root;

            for(char ch : word.toCharArray()) {
                int index = ch - 'a';

                if(node.children[index] == null) {
                    node.children[index] = new TrieNode();
                }

                node = node.children[index];
            }

            node.word = word;
        }

        List<String> result = new ArrayList<>();

        for(int i =0; i<board.length; i++) {
            for(int j=0; j<board[0].length; j++) {
                dfs(board, i, j, root, result);
            }
        }

        return result;
    }

    private void dfs(char[][] board, int row, int col, TrieNode node, List<String> result) {
        if(row < 0 || row >= board.length || col < 0 || col >= board[0].length) {
            return;
        }

        char ch = board[row][col];

        if(ch == '#') {
            return;
        }

        TrieNode next = node.children[ch - 'a'];

        // this character from board is not found in our prefix tree
        if(next == null) {
            return;
        }

        if(next.word != null) {
            result.add(next.word);
            next.word = null; // Prevent dupliate results
        }

        board[row][col] = '#';

        dfs(board, row + 1, col, next, result);
        dfs(board, row - 1, col, next, result);
        dfs(board, row, col + 1, next, result);
        dfs(board, row, col - 1, next, result);

        board[row][col] = ch;
    }
}
