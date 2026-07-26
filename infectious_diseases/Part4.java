import java.util.ArrayList;
import java.util.List;

/**
 * Part 4 - Death countdown on top of threshold infection + immunity + recovery.
 *
 * X = infected, '.' = healthy, 'I' = immune, 'D' = dead.
 *   - Immune cells are never infected and never count as infected neighbors.
 *   - Dead cells are terminal and never count as infected neighbors.
 *
 * Daily update (all decisions read the START-OF-DAY snapshot):
 *   - A '.' cell with >= infectionThreshold infected neighbors becomes infected.
 *   - ANY non-immune, non-dead cell (healthy OR infected) with >= deathThreshold
 *     infected neighbors starts a death countdown if it doesn't already have one.
 *   - Infection and countdown starts are simultaneous; a cell can be infected
 *     while a countdown is active and stays contagious until it dies.
 *
 * Recovery / death / termination:
 *   - A cell infected on day d recovers into 'I' at the end of day d + duration,
 *     but ONLY if it never started a death countdown.
 *   - A cell whose countdown starts on day d dies at the end of day d + duration.
 *     Once a countdown starts the cell can never recover.
 *   - Spread is evaluated before recovery/death; initially infected cells have
 *     infection day 0.
 *   - Continue until no infected cells and no active countdowns remain.
 * Return [daysUntilStable, totalDeaths].
 *
 * Approach (same bucket + age style as Part 3)
 * -------------------------------------------
 * age[i][j]      counts how long a cell has been infected (initial infected start
 *                at 1); a cell recovers when age == duration.
 * countdown[i][j] is 0 until a death countdown starts, then counts up; the cell
 *                dies when countdown == duration.
 *
 * Each day, from the start-of-day snapshot, fill four buckets:
 *   toInfect         '.' cells that reached infectionThreshold
 *   toStartCountdown non-immune/non-dead cells (no countdown yet) at deathThreshold
 *   toImmune         'X' cells with NO countdown whose age reached duration
 *   toDie            cells whose countdown reached duration
 * The death/recovery choice is made inline with an else-if chain: a cell that is
 * already counting down can only die, a cell that starts a countdown today is put
 * in toStartCountdown (and so is never a recovery candidate), and only otherwise
 * can an aged-out 'X' recover. New countdowns are set to 1 AFTER the increment
 * pass (like Part 3's age), so they are not bumped on their start day.
 *
 * Time:  O(A * rows * cols); Space: O(rows * cols) for the two counter grids.
 */
public class Part4 {

    // 8 neighbor directions (including diagonals).
    private static final int[][] directions = {
        {-1, -1}, {-1, 0}, {-1, 1},
        { 0, -1},          { 0, 1},
        { 1, -1}, { 1, 0}, { 1, 1}
    };

    /**
     * @param grid               plant grid ('X', '.', 'I'); 'D' appears as cells die
     * @param infectionThreshold infected neighbors for a healthy cell to be infected
     * @param deathThreshold     infected neighbors that start a death countdown
     * @param duration           days from an infection/countdown start to recovery/death
     * @return {daysUntilStable, totalDeaths}
     */
    public static int[] infectionDaysAndDeaths(
            char[][] grid, int infectionThreshold, int deathThreshold, int duration) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return new int[] {0, 0};
        }

        int rows = grid.length;
        int cols = grid[0].length;

        // age is meaningful only while a cell is 'X'; countdown == 0 means the
        // cell has never started a death countdown.
        int[][] age = new int[rows][cols];
        int[][] countdown = new int[rows][cols];
        int infected = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'X') {
                    age[i][j] = 1;
                    infected++;
                }
            }
        }

        int active = 0;   // cells with a countdown running that have not died yet
        int deaths = 0;
        int days = 0;

        // Stable once nothing can still change: no infected cells and no running
        // countdowns (a healthy cell with a countdown still dies later).
        while (infected > 0 || active > 0) {
            // 1. Bucket everything from the start-of-day snapshot.
            List<int[]> toInfect = new ArrayList<>();
            List<int[]> toStartCountdown = new ArrayList<>();
            List<int[]> toImmune = new ArrayList<>();
            List<int[]> toDie = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char c = grid[i][j];
                    if (c == 'I' || c == 'D') {
                        continue;
                    }
                    int inf = countInfectedNeighbors(grid, i, j);
                    if (c == '.' && inf >= infectionThreshold) {
                        toInfect.add(new int[] {i, j});
                    }
                    if (countdown[i][j] > 0) {
                        // Already counting down: can only die, never recover.
                        if (countdown[i][j] == duration) {
                            toDie.add(new int[] {i, j});
                        }
                    } else if (inf >= deathThreshold) {
                        // Countdown starts now, which also blocks recovery today,
                        // so this cell is never a recovery candidate.
                        toStartCountdown.add(new int[] {i, j});
                    } else if (c == 'X' && age[i][j] == duration) {
                        toImmune.add(new int[] {i, j});
                    }
                }
            }

            // 2. Kill and recover (death/recovery are keyed off the same day).
            for (int[] p : toDie) {
                if (grid[p[0]][p[1]] == 'X') {
                    infected--;
                }
                grid[p[0]][p[1]] = 'D';
                active--;
                deaths++;
            }
            for (int[] p : toImmune) {
                grid[p[0]][p[1]] = 'I';
                infected--;
            }

            // 3. Age the survivors (new infections/countdowns start at 1 below, so
            // they are added after this step, exactly like Part 3's age loop).
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (grid[i][j] == 'X') {
                        age[i][j]++;
                    }
                    if (countdown[i][j] > 0 && grid[i][j] != 'D') {
                        countdown[i][j]++;
                    }
                }
            }

            // 4. Start new countdowns and new infections at 1.
            for (int[] p : toStartCountdown) {
                countdown[p[0]][p[1]] = 1;
                active++;
            }
            for (int[] p : toInfect) {
                if (grid[p[0]][p[1]] == '.') {   // may already be 'D' if it died today
                    grid[p[0]][p[1]] = 'X';
                    age[p[0]][p[1]] = 1;
                    infected++;
                }
            }

            days++;
        }

        return new int[] {days, deaths};
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

    private static String show(int[] result) {
        return "[" + result[0] + ", " + result[1] + "]";
    }

    public static void main(String[] args) {
        // Example A: a solid 2x2 infected block. Every cell has 3 infected
        // neighbors, so with deathThreshold = 3 all four start a countdown on
        // day 1 and (duration = 2) die together at the end of day 3. The
        // countdown blocks the day-2 recovery, so all four are deaths.
        //   X X
        //   X X
        char[][] a = parse("XXXX", 2, 2);
        System.out.println(show(infectionDaysAndDeaths(a, 1, 3, 2))); // [3, 4]

        // Example B: Part 3 grid A, infectionThreshold = 2, deathThreshold = 3,
        // duration = 3. Higher death threshold means only densely-surrounded
        // cells ever die.
        //   X . I .
        //   . . . .
        //   . X . .
        char[][] b = parse("X.I......X..", 3, 4);
        System.out.println(show(infectionDaysAndDeaths(b, 2, 3, 3))); // [7, 10]

        // Example C: a lone infected cell with infectionThreshold = 2 can never
        // spread and has no infected neighbors, so it never gets a countdown and
        // simply recovers after `duration` days -> zero deaths.
        //   . . .
        //   . X .
        //   . . .
        char[][] c = parse("....X....", 3, 3);
        System.out.println(show(infectionDaysAndDeaths(c, 2, 2, 1))); // [1, 0]

        // Example D: full spread then a wave of deaths. Two opposite corners
        // seeded, infectionThreshold = 1 fills the grid, deathThreshold = 2 and
        // duration = 2 make the crowded cells die.
        //   X . .
        //   . . .
        //   . . X
        char[][] d = parse("X.......X", 3, 3);
        System.out.println(show(infectionDaysAndDeaths(d, 1, 2, 2))); // [4, 9]
    }
}
