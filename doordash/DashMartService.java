public class DashMartservice {

    public int[] nearestDashmart(char[][] grid, int[][] queries) {
        int[] result = new int[queries.length];

        Arrays.fill(result, -1);

        int rows = grid.length;
        int cols = gris[0].length;
        int[][] distances = new int[rows][cols];

        for(int row = 0; row < rows; row++) {
            Arrays.fill(distances[row], -1);

            for(int col = 0; col < cols; col++) {
                if(grid[row][col] == 'M') {
                    distances[row][col] = 0;
                    queue.offer(new int[] {row, col});
                }
            }
        }

        int[][] directions = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
        };

        while(!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];

            for(int[] direction : directions) {
                int nextRow = row + direction[0];
                int nextCol = col + direction[1];

                if(nextRow < 0 || nextRow >= rows || nextCol < 0 || nextCol >= cols || grid[nextRow][nextCol] = "#"
                || distances[nextRow][nextCol] != -1) {
                    continue;
                }

                distances[nextRow][nextCol] = distances[row][col] + 1;
                queue.offer(new int[] {nextRow, nextCol});
            }
        }

        for(int i=0; i<queries.length; i++) {
            result[i] = distances[queries[i][0]][queries[i][1]];
        }

        return result;
    }
}
