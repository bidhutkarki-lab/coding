import java.util.*;

/**
 * A freight network consists of terminals connect by direct rail routes.
 * Given a list of terminal connections and two terminal IDs, determine the minimum number of rail hops required to travel from source terminal to the destination terminal. If no route exists, return -1.
 *
 * Time: O(V + E)
 * Space: O(V + E)
 *
 * Gist: Start from the source and visit terminals level by level.
 * The first time BFS reaches the destination gives the minimum number of hops.
 *
 * Assumption: simple, directed graph with no cycles, gurantted to be connected
 */
public class RailNetwork {

    public static int minimumHops(
            List<List<String>> connections,
            String source,
            String destination) {

        if (source.equals(destination)) {
            return 0;
        }

        Map<String, List<String>> graph = new HashMap<>();

        // undirected adjacency list
        for (List<String> connection : connections) {
            String terminal1 = connection.get(0);
            String terminal2 = connection.get(1);

            graph.computeIfAbsent(terminal1, key -> new ArrayList<>())
                    .add(terminal2);

            graph.computeIfAbsent(terminal2, key -> new ArrayList<>())
                    .add(terminal1);
        }

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.offer(source);
        visited.add(source);

        int hops = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            hops++;

            for (int i = 0; i < levelSize; i++) {
                String current = queue.poll();

                for (String neighbor :
                        graph.getOrDefault(current, Collections.emptyList())) {

                    if (neighbor.equals(destination)) {
                        return hops;
                    }

                    // handle cycle
                    if (visited.add(neighbor)) {
                        queue.offer(neighbor);
                    }
                }
            }
        }

        return -1;
    }

    private static void main(String[] args) {
        List<List<String>> connections = List.of(
                List.of("A", "B"),
                List.of("B", "C"),
                List.of("A", "D"),
                List.of("D", "C")
        );

        System.out.println(
                RailNetwork.minimumHops(connections, "A", "C")
        ); // 2

}

/**
 * What if there is weight on the edges?
 * - Use dijkstra algorithm
 *
 * What if there are millions of terminals?
 *
 * Memory:
 *  - Use adjancency list instead of adjancey matrix
 *  - use redis key/value pair instead of application in memory
 *  - avoid loading full graph, service can fetch neigbors only for terminals currently being explored
 *
 * Partioning
 * - partionion by region, if route crosses regions, the routing service communicates with other partions
 *
 * Distributed graph processing
 * - Each workers can process graph based on partions
 * - Maintain global visited set and avoid duplicate work
 *
 * Precomute shortest path with caching
 * - shorted paths between major hubs
 * - recently requeseted source-destination results
 *
 * How would be handle this in production system:
 *
 * - Same as above, but also route updates happens through kafka, validation before update
 * - Route data will come from central database
 */
