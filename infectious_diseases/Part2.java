import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Part 2 - Infection with immunity.
 *
 * X = infected plant, '.' = healthy plant, 'I' = immune plant.
 * Immune plants are never infected and block the spread (8-neighbor).
 * Returns the number of days until no new infections happen, or -1 if some
 * healthy plant can never be reached (walled off by immune plants).
 *
 * This is Part 1 almost unchanged. The BFS only ever spreads into '.', so 'I'
 * already blocks spread for free -- we never overwrite it. The ONE change:
 * count only '.' as healthy; 'I' must not be counted as a cell to infect,
 * otherwise the saturation/answer logic would wait forever on an immune cell.
 */
public class Part2 {

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
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'X') {
                    queue.add(new int[] {i, j, 0});
                } else if (grid[i][j] == '.') {   // <-- the one change: 'I' is not counted
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
                        || grid[newX][newY] != '.') {   // 'I' blocks spread for free
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

        // Some healthy plant was walled off by immune plants and never infected.
        return -1;
    }

    // Builds a grid from a flat string, e.g. "X..I.....IX." with 3 rows x 4 cols.
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
        // Example A: immunity slows but doesn't block.
        //   X . I .
        //   . . . .
        //   . X . .
        char[][] a = parse("X.I......X..", 3, 4);
        System.out.println(daysUntilStable(a)); // 2

        // Example B: a healthy cell walled off on all 8 sides -> -1.
        //   X . . .
        //   . . I I
        //   . . I .
        // The '.' at (2,3) has only 3 in-grid neighbors -- (1,2), (1,3), (2,2) --
        // and all are immune, so infection can never reach it. Everything else
        // does get infected, so healthy never hits 0 and we return -1.
        char[][] b = parse("X.....II..I.", 3, 4);
        System.out.println(daysUntilStable(b)); // -1
    }
}
