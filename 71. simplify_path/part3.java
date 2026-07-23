import java.util.*;

/*
 * Leetcode 71 Simplify Path Variant - Part 3: Symbolic links
 *
 * cd(current_dir: str, destination: str, home_dir: str, symlinks: Map<str,str>) -> str
 *
 * Same shape as Part 2, plus symlink resolution:
 *   - After pushing an ordinary segment, if the running prefix is a symlink key,
 *     replace the whole stack with the target's normalized segments and continue.
 *   - Follow chained symlinks; guard cycles with a visited set (raise on re-entry).
 *   - ".." is applied to the post-expansion stack, so backing out a symlink goes
 *     through its target's parent, not lexically.
 */
public class part3 {

    private static String cd(String currentDir, String dest, String homeDir,
                             Map<String, String> symlinks) {

        // Part 2 resolution: pick the starting absolute path
        String path;
        if (dest.startsWith("~")) {
            if (dest.equals("~") || dest.startsWith("~/")) {
                path = homeDir + dest.substring(1);
            } else {
                throw new IllegalArgumentException("~user is out of scope: " + dest);
            }
        } else if (dest.startsWith("/")) {
            path = dest;
        } else {
            path = currentDir + "/" + dest;
        }

        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>(); // symlink keys already entered

        for (String dir : path.split("/")) {
            if (dir.equals("") || dir.equals(".")) {
                continue;
            } else if (dir.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else {
                stack.push(dir);

                // Part 3: resolve a symlink chain on the running prefix
                String prefix = "/" + String.join("/", stack);
                while (symlinks.containsKey(prefix)) {
                    if (!visited.add(prefix)) {
                        throw new IllegalStateException("symlink cycle detected: " + prefix);
                    }
                    stack = normalize(symlinks.get(prefix));
                    prefix = "/" + String.join("/", stack);
                }
            }
        }

        return "/" + String.join("/", stack);
    }

    // symlink can be also raw path
    private static Stack<String> normalize(String target) {
        Stack<String> stack = new Stack<>();
        for (String dir : target.split("/")) {
            if (dir.equals("") || dir.equals(".")) {
                continue;
            } else if (dir.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else {
                stack.push(dir);
            }
        }
        return stack;
    }

    public static void main(String[] args) {
        // spec example
        Map<String, String> s1 = new HashMap<>();
        s1.put("/home/alice/latest", "/mnt/releases/2026-03-01");
        System.out.println(cd("/home/alice", "latest/logs/../config", "/home/alice", s1));
        // -> /mnt/releases/2026-03-01/config

        // ".." backs out THROUGH the symlink target, thats why no pre-normalize
        // pre-normalizing before resolution gives wrong answer for path that crosses a symblink and then backs out with ..
        System.out.println(cd("/home/alice", "latest/..", "/home/alice", s1));
        // -> /mnt/releases

        // chained symlinks
        Map<String, String> s2 = new HashMap<>();
        s2.put("/a", "/b");
        s2.put("/b", "/x/y");
        System.out.println(cd("/", "/a/z", "/home", s2)); // -> /x/y/z

        // cycle -> raise
        Map<String, String> s3 = new HashMap<>();
        s3.put("/a", "/b");
        s3.put("/b", "/a");
        try {
            cd("/", "/a", "/home", s3);
            System.out.println("ERROR: expected ELOOP");
        } catch (IllegalStateException e) {
            System.out.println("raised as expected: " + e.getMessage());
        }

        // no symlinks -> behaves like Part 2
        System.out.println(cd("/usr/local", "~/projects", "/home/alice", new HashMap<>()));
        // -> /home/alice/projects
    }
}
