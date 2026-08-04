public class RailNetwork {

    record Edge(string destination, int weight) {}

    record State(String terminal, int totalCost) {}

    public static int minimumCost(
        List<List<Object>> connections,
        String source,
        String destination
    ) {
        if(source.equals(destination)) {
            return 0;
        }

        Map<String, List<Edge>> graph = new HashMap<>();

        for(List<Object> connection : connections) {
            String from = (String) connection.get(0);
            String to = (String) connection.get(1);
            int weight = (Integer) connection.get(2);

            graph.computeIfAbset(from, key -> new ArrayList<>()).add(new Edge(to, weight));
        }

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(State::totalCost));

        Map<String, Integer> minimumCosts = new HashMap<>();

        queue.offer(new State(source, 0));
        minimumCost.put(source, 0);

        while(!queue.isEmpty()) {
            State current = queue.poll();

            // a terminal may appear in the queue more than once.
            // skip this entry if a cheaper route has already been discovered.
            if(current.totalCost() > minimumCosts.getOrDefault(current.terminal(), Integer.MAX_VALUE)) {
                continue;
            }

            if(current.terminal().equals(destination)) {
                return current.totalCost();
            }

            for(Edge neighbor : graph.getOrDefault(current.terminal(), Collections.emptyList())) {

                int newCost = current.totalCost() + neighbor.weight();
                int knownCost = minimumCost.getOrDefault(neighbor.destination(), Integer.MAX_VALUE);

                // relax the edge when the new route is cheaper
                if(newCost < knownCost) {
                    minimumCost.put(neighbor.destination(), newCost);
                    queue.offer(new State(edge.destination(), newCost));
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        List<List<Object>> connections = List.of(
                List.of("A", "B", 5),
                List.of("A", "C", 2),
                List.of("C", "B", 1),
                List.of("B", "D", 3),
                List.of("C", "D", 10)
        );

        System.out.println(
                minimumCost(connections, "A", "D")
        ); // 6: A -> C -> B -> D
    }
}
}
