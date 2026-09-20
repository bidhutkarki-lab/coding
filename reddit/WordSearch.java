/**
Leetcode 79. Word search

Use DFS with backtracking.
Start from every cell with word index 0.
Current cell is visited now. Search on all the direction for a matching next character.
Make that cell unvisited again after search from there is complete (backtracking).

Time: O(R × C × 3ᴸ) — try every cell, with at most 3 onward choices after the first move.
Space: O(L) — recursion stack.

*/
class WordSearch {
    int rows, cols;

    public boolean exist(char[][] board, String word) {
        rows = board.length;
        cols = board[0].length;

        for(int i=0; i<rows; i++) {
            for(int j = 0; j<cols; j++) {
                if(dfs(board, word,i, j, 0)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean dfs(char[][] board, String word, int row, int col, int index) {
        if(row < 0 || row >= rows || col < 0 || col >= cols || board[row][col] != word.charAt(index)) {
            return false;
        }

        if(index == word.length()-1) {
            return true;
        }

        char temp = board[row][col];
        board[row][col] = '#'; // Mark visited for this path

        boolean found = dfs(board, word, row+1, col, index+1) ||
        dfs(board, word, row-1, col, index+1) ||
        dfs(board, word, row, col+1, index+1) ||
        dfs(board, word, row, col-1, index+1);

        board[row][col] = temp; // Backtrack after processing that cell
        return found;
    }
}
