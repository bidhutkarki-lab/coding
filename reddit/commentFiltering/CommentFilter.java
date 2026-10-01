import java.util.*;

public class CommentFilter {

    enum Mode {
        CAT_PERSON,
        DOG_PERSON
    }

    record Comment (
        int id,
        Integer parentComment,
        String body,
        boolean cat,
        boolean dog
    ) {}

    public Set<Integer> getCommentsToExclude(Map<Integer, Comment> comments, Mode mode) {
        Objects.requireNonNull(mode, "mode must not be null");

        Map<Integer, List<Integer>> children = new HashMap<>();
        Set<Integer> excluded = new HashSet<>();
        Stack<Integer> stack = new Stack<>();

        for(Comment comment : comments.values()) {
            // Build the parent-to-children map
            if(comment.parentComment != null) {
                children.computeIfAbsent(comment.parentComment, key -> new ArrayList<>()).add(comment.id);
            }

            boolean undesired = mode == Mode.CAT_PERSON ? comment.dog : comment.cat;

            // exclude the undesired comment
            if(undesired && excluded.add(comment.id)) {
                stack.push(comment.id);
            }
        }

        // fromt the undesired comment, exclude it's children too
        while(!stack.isEmpty()) {
            int id = stack.pop();

            for(int childId : children.getOrDefault(id, Collections.emptyList())) {
                if(excluded.add(childId)) {
                    stack.push(childId);
                }
            }
        }

        return excluded;
    }
}
