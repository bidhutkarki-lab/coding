import java.util.*;

/*
 * Leetcode 71 Simplify Path Variant - Part 2: Absolute paths and ~
 *
 * cd(current_dir: str, destination: str, home_dir: str) -> str
 *
 * destination may be:
 *   - relative            -> applied on top of current_dir
 *   - absolute (starts /) -> used as-is
 *   - begins with ~       -> the home directory (home_dir)
 *
 * ~ is special ONLY as the first character. ~user (a tilde immediately
 * followed by a name) is out of scope and must raise.
 */
public class part2 {

    private static String cd(String currentDir, String dest, String homeDir) {

        // resolve the base path depending on destination type
        String path;
        if (dest.startsWith("~")) {
            if (dest.equals("~") || dest.startsWith("~/")) {
                path = homeDir + dest.substring(1); // "~" -> homeDir; "~/x" -> homeDir + "/x"
            } else {
                throw new IllegalArgumentException("~ use is out of scope: " + dest);
            }
        } else if (dest.startsWith("/")) {
            path = dest;
        } else {
            path = currentDir + "/" + dest;
        }

        // simplify path (OG)
        Stack<String> stack = new Stack<>();

        for (String dir : path.split("/")) {
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

        return "/" + String.join("/", stack);
    }

    public static void main(String[] args) {
        System.out.println(cd("/usr/local", "/etc/nginx", "/home/alice"));  // /etc/nginx
        System.out.println(cd("/usr/local", "~/projects", "/home/alice"));  // /home/alice/projects
        System.out.println(cd("/usr/local", "~", "/home/alice"));           // /home/alice
        System.out.println(cd("/usr/local", "../lib", "/home/alice"));      // /usr/lib

        try {
            cd("/usr/local", "~alice/x", "/home/alice"); // must raise
            System.out.println("ERROR: expected exception for ~user");
        } catch (IllegalArgumentException e) {
            System.out.println("raised as expected: " + e.getMessage());
        }
    }
}
