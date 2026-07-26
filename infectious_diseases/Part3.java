import java.util.ArrayList;
import java.util.List;

/**
 * Part 3 - Threshold infection + immunity + recovery.
 *
 * Complexity (N = rows * cols, A = days it takes to become stable):
 *   Time:  O(A * rows * cols)
 *          Each day scans every cell once and each 8-neighbor check is O(1),
 *          so a day costs O(rows * cols); we run A days until no active
 *          infection remains. A itself is O(N + D) in the worst case.
 *   Space: O(rows * cols)
 *          The `age` array (the grid is mutated in place); the per-day
 *          `toInfect` / `toImmune` lists hold at most O(rows * cols) cells.
 *
 * X = infected plant, '.' = healthy plant, 'I' = immune plant.
 *   - A healthy cell becomes infected on a day when it has >= T infected
 *     neighbors (of its 8).
 *   - Pre-existing immune plants are never infected and block spread.
 *   - After being infected for D days a cell RECOVERS into immunity ('I'): it
 *     stops spreading and can never be re-infected.
 * Return the number of days until no *active* infection remains.
 *
 * Approach
 * --------
 * Day-by-day synchronous simulation. From the START-OF-DAY snapshot:
 *
 * Every day,
 *  1. scan the grid and fill toImmune and toInfect bucket
 *      1.1 toImmune: X cell which age is greater than D (recovery days)
 *      1.2 toInfect: '.' cell which neighbors are greater than T (infection threshold)
 *  2. go through toImmune bucket and mark those cell as I and increase immune count
 *  3. go through toInfect bucket and mark those cell as X and increase infected count
 *  4. Also, increase age of all infected cells in age grid
 *  5. return the days it there is no infection left
 *
 * Time: O(A * rows * cols) —  A = days until stable (A is O(N + D) worst case).
 * Space: O(rows * cols) — the age array plus the per-day change lists.
 *
 */
public class Part3 {

    // 8 neighbor directions (including diagonals).
    private static final int[][] directions = {
        {-1, -1}, {-1, 0}, {-1, 1},
        { 0, -1},          { 0, 1},
        { 1, -1}, { 1, 0}, { 1, 1}
    };

    /**
     * @param grid the plant grid ('X' infected, '.' healthy, 'I' immune)
     * @param t    infected neighbors required for a healthy cell to be infected
     * @param d    days a cell stays infected before recovering into immunity
     * @return days until no active infection remains
     */
    public static int daysUntilStable(char[][] grid, int t, int d) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int rows = grid.length;
        int cols = grid[0].length;

        // age[i][j] is meaningful only while grid[i][j] == 'X'; initial infected
        // cells start at age 1 and recover when their age reaches D.
        int[][] age = new int[rows][cols];
        int infected = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'X') {
                    age[i][j] = 1;
                    infected++;
                }
            }
        }

        int days = 0;

        // Simulate until no active infection remains.
        while (infected > 0) {
            // Both passes read the start-of-day snapshot before any mutation.
            List<int[]> toInfect = new ArrayList<>();
            List<int[]> toImmune = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (grid[i][j] == '.') {
                        if (countInfectedNeighbors(grid, i, j) >= t) {
                            toInfect.add(new int[] {i, j});
                        }
                    } else if (grid[i][j] == 'X' && age[i][j] == d) {
                        toImmune.add(new int[] {i, j});
                    }
                }
            }

            // Recover cells that have hit D days.
            for (int[] p : toImmune) {
                grid[p[0]][p[1]] = 'I';
                infected--;
            }

            // Age the infected cells that survive to another day.
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (grid[i][j] == 'X') {
                        age[i][j]++;
                    }
                }
            }

            // Infect the healthy cells that reached the threshold (age 1 today).
            for (int[] p : toInfect) {
                grid[p[0]][p[1]] = 'X';
                age[p[0]][p[1]] = 1;
                infected++;
            }

            days++;
        }

        return days;
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
        // Example A: Part 2 grid A, T = 2, D = 3.
        //   X . I .
        //   . . . .
        //   . X . .
        char[][] a = parse("X.I......X..", 3, 4);
        System.out.println(daysUntilStable(a, 2, 3)); // 7

        // Example B: Part 2 grid B (has an immune wall), T = 1, D = 2.
        //   X . . .
        //   . . I I
        //   . . I .
        char[][] b = parse("X.....II..I.", 3, 4);
        System.out.println(daysUntilStable(b, 1, 2)); // 5

        // Example C: Part 1 grid, T = 1, D = 1.
        //   X . . .
        //   . . . .
        //   . X . .
        char[][] c = parse("X.........X.", 3, 4);
        System.out.println(daysUntilStable(c, 1, 1)); // 3

        // Example D: threshold + recovery makes the infection die out early with
        // healthy cells still left -- finite answer, no -1. A lone X with T = 2
        // can never spread and recovers after D = 1 day.
        //   . . .
        //   . X .
        //   . . .
        char[][] d = parse("....X....", 3, 3);
        System.out.println(daysUntilStable(d, 2, 1)); // 1
    }
}
