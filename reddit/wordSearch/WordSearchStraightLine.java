/**
 * Try every starting cell and all eight directions.
 * Compares each character while moving in the same direction.
 * Return false on a mismatch or boundary, and true if the whole word matches.
 * No visited tracking is needed because a straight line never revisits a cell.
 *
 * Time: O(R × C × L)
 * Extra space: O(1)
 */
public class WordSearchStraightLine {
    private static final int[][] DIRECTIONS = {
        {0, 1}, {0, -1},
        {1, 0}, {-1, 0},
        {1, 1}, {1, -1},
        {-1, 1}, {-1, -1}
    };

    int rows, cols;

    public boolean exist(char[][] board, String word) {

        rows = board.length;
        cols = board[0].length;

        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                if(board[i][j] != word.charAt(0)) {
                    continue;
                }

                for(int[] DIRECTIONS : DIRECTIONS) {
                    if(checkStraightLine(board, word, i, j, dir[0], dir[1])) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkStraightLine(char[][] board, String word, int row, int col, int dirRow, int dirCol) {

        for(int i=0; i<word.length(); i++) {
            if(row < 0 || row >= rows || col < 0 || col >= cols || board[row][col] != word.charAt(i)) {
                return false;
            }

            row += dirRow;
            col += dirCol;
        }

        return true;
    }
}
