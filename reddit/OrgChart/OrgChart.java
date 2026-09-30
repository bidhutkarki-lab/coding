import java.util.*;

/**
 * Phase 1:
 * A Map<String, List<String>> to store each manager’s direct reports.
 * A Set<String> to track employees who have a manager. The root is the person missing from this set.
 * DFS to print each employee with "...." repeated by their depth.
 * Time: O(N) to build; O(N + C) to render, where C is the output size.
 * Space: O(N) for the chart; O(H + C) to render, including the output.
 *
 * Phase 2:
 * Reuse the children map from Phase 1.
 * For every manager, look at each direct report’s children.
 * Those employees are exactly two levels below the manager.
 * Time: O(N + K), where K is the number of skip-level pairs.
 * Space: O(K) for the result; O(1) auxiliary space.
 *
 * Phase 3:
 * Keep a parent map so you can walk from the target back to the root.
 * Reverse that path, print the ancestors, then reuse Phase 1’s render() to print the target and its subtree.
 * Time: O(A + S + C), where A is the path length, S is the subtree size,
 * and C is the output size.
 * Space: O(A + R + C), where R is the target subtree height.
 *
 * Phase 4:
 * Use the parent map
 * Store the first employee and all their ancestors in a set.
 * Walk upward from the second employee. The first person found in that set is the lowest common manager.
 * Time: O(H).
 * Space: O(H).
 *
 * Alternate Version 1: Ancestor Check, return whether one employee is a strict ancestor of another.
 * Walk upward from the employee’s parent. Starting at the parent ensures an employee is not their own ancestor.
 *
 * Time: O(h), where h is the tree height.
 * Extra space: O(1).
 *
 * Alternate Version 2: Strict Common Manager, the answer must be an ancestor of both employees, excluding the employees themselves.
 * Use the Phase 4 approach, but start both walks from each employee’s parent.
 *
 * Extension 2: Serialize the Organization, convert the tree back into a List<List<String>>.
 * Each row contains a manager followed by their direct reports.
 * Use preorder DFS to emit each manager’s row before visiting their reports.
 * when the root is the only employee, no need to do anything just return back
 */
public class OrgChart {

    private final Map<String, List<String>> children = new HashMap<>();
    private final Map<String, String> parent = new HashMap<>();
    private final String root;

    public OrgChart(List<List<String>> relations) {

        Set<String> hasManager = new HashSet<>();

        for(List<String> row : relations) {
            String manager = row.get(0);
            children.computeIfAbsent(manager, key -> new ArrayList<>());

            for(int i=1; i<row.size(); i++) {
                String report = row.get(i);

                children.get(manager).add(report);
                parent.put(report, manager);
                children.computeIfAbsent(report, key-> new ArrayList<>());
                hasManager.add(report);
            }
        }

        String rootCandidate = null;

        for(String employee : children.keySet()) {
            if(!hasManager.contains(employee)) {
                if(rootCandidate != null) {
                    throw new IllegalArgumentException("Multiple roots found");
                }
                rootCandidate = employee;
            }
        }

        if(rootCandidate == null) {
            throw new IllegalArgumentException("No root found");
        }

        root = rootCandidate;
    }

    public OrgChat(List<List<String>> relations) {
        if (relations == null || relations.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }

        for(List<String> row : relations) {
            if(row == null || row.isEmpty()) {
                throw new IllegalArgumentException("Row cannot be empty");
            }

            for(String name: row) {
                if(name == null || name.isBlank()) {
                    throw new IllegalArgumentException("Employee name cannot be blank");
                }
            }

            String manager = row.get(0);
            if(!managersSeen.add(manager)) {
                "Repeated manager row: " + manager
            }

            children.computeIfAbsent(manager, key -> new ArrayList<>());
            Set<String> reportsSeen = new HashSet<>();

            for(int i=1; i<row.size(); i++) {
                String report = row.get(i);

                if(manager.equals(report)) {
                    throw new IllegalArgumentException("Employee cannot report to themselves: " + manager);
                }

                if(!reportsSeen.add(report)) {
                    throw new IllegalArgumentException("Duplicate reporting edge: " + manager + " -> " + report);
                }

                if(parent.containsKey(report)) {
                    throw new IllegalAgurmentException("Employee has multiple managers: " + report);
                }

                parent.put(report, manager);
                children.get(manager).add(report);
                children.computeIfAbsent(report, key -> new ArrayList<>());
            }
        }

        // find the root
        String rootCandidate = null;
        for (String employee : children.keySet()) {
            if (!parent.containsKey(employee)) {
                if (rootCandidate != null) {
                    throw new IllegalArgumentException("Multiple roots found");
                }

                rootCandidate = employee;
            }
        }

        if (rootCandidate == null) {
            throw new IllegalArgumentException("No root found; input contains a cycle");
        }

        validateReachability(rootCandidate);
        root = rootCandidate;
    }

    // Why check reachability when we already found one root?
    // This input has exactly one root, A, but also a disconnected cycle
    // A -> B, C->D, D->C
    private void validateReachability(String root) {
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            String employee = stack.pop();

            if (!visited.add(employee)) {
                throw new IllegalArgumentException(
                    "Cycle or repeated node found: " + employee
                );
            }

            for (String report : children.get(employee)) {
                stack.push(report);
            }
        }

        if (visited.size() != children.size()) {
            throw new IllegalArgumentException(
                "Employees are unreachable from the root; "
                    + "input contains a disconnected cycle"
            );
        }
    }

    public String renderFullChain() {
        StringBuilder result = new StringBuilder();
        render(root, 0, result);
        return result.toString();
    }

    private void render(String employee, int depth, StringBuilder result) {
        if(result.length() > 0) {
            result.append('\n');
        }

        result.append("....".repeat(depth))
        .append(employee);

        for(String report : children.get(employee)) {
            render(report, depth + 1, result);
        }
    }

    // PART 2
    public record SkipLevelPair(String manger, String employee) {}

    public List<SkipLevelPair> allSkipLevelPairs() {
        List<SkipLevelPair> result = new ArrayList<>();

        for(String manager : children.keySet()) {
            for(String directReport : children.get(manager)) {
                for(String employee : children.get(directReport)) {
                    result.add(new SkipLevelPair(manager, employee));
                }
            }
        }

        return result;
    }

    // PART 3
    public String renderChainFor(String target) {
        if(!children.containsKey(target)) {
            throw new IllegalArgumentException("Unknown employee: " + target);
        }

        List<String> path = new ArrayList<>();
        String current = target;

        while(current != null) {
            path.add(current);
            current = parent.get(current);
        }

        Collections.reverse(path);

        StringBuilder result = new StringBuilder();

        // Pring only the ancestors. leave the target for render();
        for(int depth = 0; depth < path.size() -1; depth++) {
            if(result.length() > 0) {
                result.append("\n");
            }

            result.append("....".repeat(depth))
            .append(path.get(depth));
        }

        render(target, path.size()-1, result);

        return result.toString();
    }

    public String lowestCommonManager(String e1, String e2) {
        if(!children.containsKey(e1) || !children.containsKey(e2)) {
            throw new IllegalArgumentException("Unknown employee");
        }

        Set<String> ancestors = new HashSet<>();

        String current = e1; // if it was not inclusive current = parent.get(e1)
        while(current != null) {
            ancestors.add(current);
            current = parent.get(current);
        }

        current = e2; // if not incluse current = parent.get(e2)
        while(current != null) {
            if(ancestors.contains(current)) {
                return current;
            }
            current = parent.get(current);
        }

        throw new IllegalStateException("No common manager found");
    }

    // Alternate version 2
    public Optional<String> lowestStrictCommonManager(String e1, String e2) {
        if(!children.containsKey(e1) || !children.containsKey(e2)) {
            throw new IllegalAgurmentException("Unknown employee");
        }

        Set<String> ancestors = new HashSet<>();

        String current = parent.get(e1);
        while(current != null) {
            ancestors.add(current);
            current = parent.get(current);
        }

        current = parent.get(e2);
        while(current != null) {
            if(ancestors.contains(current)) {
                return Optional.of(current);
            }
            current = parent.get(current);
        }

        return Optional.empty();
    }

    // Alternate version 1
    public boolean isAncestor(String ancestor, String employee) {
        if(!children.containsKey(ancestor) || !children.containsKey(employee)) {
            throw new IllegalArgumentException("Unknown employee");
        }

        String current = parent.get(employee);

        while(current != null) {
            if(current.equals(ancestor)) {
                return true;
            }

            current = parent.get(current);
        }

        return false;
    }

    public List<String> employessAtLevel(String manager, int levels) {
        if(!children.containsKey(manager)) {
            throw new IllegalArgumentException("Unknown manager: " + manager);
        }
        if(levels < 0) {
            throw new IllegalAgurmentException("Levels cannot be negative");
        }

        Queue<String> queue = new ArrayDeque<>();
        queue.offer(manager);

        for(int i = 0; i<levels && !queue.isEmpty(); i++) {
            int size = queue.size();

            for(int i=0; i<size; i++) {
                String poll = queue.poll();

                for(String report : children.get(employee)) {
                    queue.offer(report);
                }
            }
        }

        return new ArrayList<>(queue);
    }

    public List<List<String>> serialize() {
        List<List<String>> result = new ArrayList<>();

        // if root has no children
        if(children.get(root).isEmpty()) {
            result.add(List.of(root));
            return result;
        }

        serialize(root, result);
        return result;
    }

    // preorder dfs
    private void serialize(String employee, List<List<String>> result) {
        List<String> reports = children.get(employee);

        if(reports.isEmpty()) {
            return;
        }

        List<String> row = new ArrayList<>();
        row.add(employee);
        row.addAll(reports);
        result.add(row);

        for(String report : reports) {
            serialize(report, result);
        }
    }


    public static void main(String[] args) {
        List<List<String>> relations = List.of(
            List.of("A", "B", "C"),
            List.of("C", "D"),
            List.of("B", "E")
        );

        OrgChart org = new OrgChart(relations);


        System.out.println(org.renderFullChain());

        // PART 2
        System.out.println(org.allSkipLevelPairs());

        // PART 3
        System.out.println(org.renderChainFor("B"));

        // PART 4 LCM
        org.lowestCommonManager("C", "E"); // A
        org.lowestCommonManager("B", "E"); // B
        org.lowestCommonManager("E", "E"); // E

        // Alternate version 1
        org.isAncestor("A", "E"); // true: E → B → A
        org.isAncestor("B", "E"); // true: E → B
        org.isAncestor("E", "B"); // false
        org.isAncestor("B", "C"); // false
        org.isAncestor("B", "B"); // false: strict ancestor

        // Alternate version 2
        org.lowestStrictCommonManager("C", "E"); // Optional[A]
        org.lowestStrictCommonManager("B", "E"); // Optional[A]
        org.lowestStrictCommonManager("E", "E"); // Optional[B]
        org.lowestStrictCommonManager("A", "D"); // Optional.empty

        // Extension 1
        org.employeesAtLevel("A", 0); // [A]
        org.employeesAtLevel("A", 1); // [B, C]
        org.employeesAtLevel("A", 2); // [E, D]
        org.employeesAtLevel("A", 3); // []
        org.employeesAtLevel("B", 1); // [E]


    }
}
