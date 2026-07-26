import java.util.ArrayList;
import java.util.List;

/**
 * Part 2B - Immunity + infection threshold T (no recovery).
 *
 * X = infected plant, '.' = healthy plant, 'I' = immune plant.
 *   - A healthy cell becomes infected on a day when it has >= T infected
 *     neighbors (of its 8). T = 1 is exactly Part 2.
 *   - Immune plants are never infected and block spread (they are never counted
 *     as infected neighbors).
 *   - There is NO recovery: once infected a cell stays 'X' forever.
 * Return the number of days until no new infection can happen, or -1 if some
 * healthy plant can never be infected.
 *
 * Complexity (N = rows * cols, A = days it takes to become stable):
 *   Time:  O(A * rows * cols) -- each day scans every cell with O(1) neighbor
 *          checks; A is O(N) since spread is monotonic ('.' -> 'X' only).
 *   Space: O(rows * cols) for the per-day toInfect list (grid mutated in place).
 *
 * Why this needs a simulation (unlike Part 2)
 * -------------------------------------------
 * With T = 1 a single infected neighbor is enough, so infection spreads along
 * shortest paths and the answer is just the max BFS distance (Part 2). With
 * T > 1 whether a cell gets infected depends on HOW MANY of its neighbors are
 * already infected, which changes day to day, so a plain distance BFS no longer
 * works. Instead simulate day by day: each day, from the START-OF-DAY snapshot,
 * find every '.' cell that has reached the threshold and infect them together.
 *
 * Infection is monotonic ('.' -> 'X' only), so the process always reaches a
 * fixed point. When a day produces no new infections we stop: if every healthy
 * cell got infected, the last active day is the answer; if any '.' remains, it
 * can never be infected, so return -1.
 */
public class Part2B {

    // 8 neighbor directions (including diagonals).
    private static final int[][] directions = {
        {-1, -1}, {-1, 0}, {-1, 1},
        { 0, -1},          { 0, 1},
        { 1, -1}, { 1, 0}, { 1, 1}
    };

    public static int daysUntilStable(char[][] grid, int t) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int rows = grid.length;
        int cols = grid[0].length;

        int healthy = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '.') {
                    healthy++;
                }
            }
        }

        // No healthy cells to infect => already stable.
        if (healthy == 0) {
            return 0;
        }

        int days = 0;

        // Simulate one day at a time.
        while (true) {
            // Detect all newly-infected cells from the start-of-day snapshot, so
            // infections within the same day do not chain off one another.
            List<int[]> toInfect = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (grid[i][j] == '.' && countInfectedNeighbors(grid, i, j) >= t) {
                        toInfect.add(new int[] {i, j});
                    }
                }
            }

            // No cell reached the threshold today => fixed point reached.
            if (toInfect.isEmpty()) {
                break;
            }

            for (int[] p : toInfect) {
                grid[p[0]][p[1]] = 'X';
                healthy--;
            }
            days++;

            if (healthy == 0) {   // everything infected, stop early
                return days;
            }
        }

        // Some healthy plant can never reach the threshold => never infected.
        return healthy == 0 ? days : -1;
    }

    private static int countInfectedNeighbors(char[][] grid, int r, int c) {
        int count = 0;
        for (int[] direction : directions) {
            int nr = r + direction[0];
            int nc = c + direction[1];
            if (nr < 0 || nc < 0 || nr >= grid.length || nc >= grid[0].length
                    || grid[nr][nc] != 'X') {
                continue;
            }
            count++;
        }
        return count;
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
        // Example A: Part 2 example A with T = 1 -> identical to Part 2 (2).
        //   X . I .
        //   . . . .
        //   . X . .
        char[][] a1 = parse("X.I......X..", 3, 4);
        System.out.println(daysUntilStable(a1, 1)); // 2

        // Same grid with T = 2: a healthy cell now needs 2 infected neighbors,
        // so spread is slower but still completes.
        char[][] a2 = parse("X.I......X..", 3, 4);
        System.out.println(daysUntilStable(a2, 2)); // 4

        // Example B: Part 2 example B -- (2,3) is walled off by immune cells.
        //   X . . .
        //   . . I I
        //   . . I .
        // Never fully infects, so -1 (holds for any T).
        char[][] b = parse("X.....II..I.", 3, 4);
        System.out.println(daysUntilStable(b, 1)); // -1

        // Example C: T = 2 with two opposite corners seeded. The center gets 2
        // infected neighbors and the infection cascades to fill the grid.
        //   X . .
        //   . . .
        //   . . X
        char[][] c = parse("X.......X", 3, 3);
        System.out.println(daysUntilStable(c, 2)); // 3

        // Example D: a single infected cell with T = 2 can never spread at all
        // (every neighbor has only 1 infected neighbor) -> -1.
        //   . . .
        //   . X .
        //   . . .
        char[][] d = parse("....X....", 3, 3);
        System.out.println(daysUntilStable(d, 2)); // -1
    }
}
