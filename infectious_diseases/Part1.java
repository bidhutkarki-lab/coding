import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Part 1 - Basic infection.
 *
 * X = infected plant, '.' = healthy plant.
 * Each day every infected plant infects all 8 neighbors.
 * Returns the number of days until no new infections happen.
 *
 * Core idea: multi-source BFS seeded from every initial X at once.
 * Each queued cell carries the day it was infected; because BFS expands in
 * nondecreasing day order, the last cell infected carries the largest day,
 * which is the answer.
 */
public class Part1 {

    // 8 neighbor directions (including diagonals).
    private static final int[][] directions = {
        {-1, -1}, {-1, 0}, {-1, 1},
        { 0, -1},          { 0, 1},
        { 1, -1}, { 1, 0}, { 1, 1}
    };

    public static int daysUntilStable(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int rows = grid.length;
        int cols = grid[0].length;

        Deque<int[]> queue = new ArrayDeque<>();
        int healthy = 0;

        // Seed the queue with every infected cell (day 0); count the healthy ones.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 'X') {
                    queue.add(new int[] {r, c, 0});
                } else {
                    healthy++;
                }
            }
        }

        // No healthy cells (or no infected cells) => already stable.
        if (healthy == 0) {
            return 0;
        }

        int max = 0;

        // Multi-source BFS. Each cell carries the day it was infected.
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentTime = current[2];

            for (int i = 0; i < directions.length; i++) {
                int newX = current[0] + directions[i][0];
                int newY = current[1] + directions[i][1];
                if (newX < 0 || newX >= rows || newY < 0 || newY >= cols
                        || grid[newX][newY] != '.') {
                    continue;
                }

                grid[newX][newY] = 'X';
                healthy--;

                max = Math.max(currentTime + 1, max);
                queue.add(new int[] {newX, newY, currentTime + 1});

                if (healthy == 0) {       // grid saturated, stop early
                    return max;
                }
            }
        }

        return max;
    }

    // Builds a grid from a flat string, e.g. "X.........X." with 3 rows x 4 cols.
    private static char[][] parse(String flat, int rows, int cols) {
        char[][] grid = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = flat.charAt(r * cols + c);
            }
        }
        return grid;
    }

    public static void main(String[] args) {
        // Example: 3x4 grid "X.........X."
        //   X . . .
        //   . . . .
        //   . X . .
        char[][] grid = parse("X.........X.", 3, 4);
        System.out.println(daysUntilStable(grid)); // Expected: 2
    }
}
