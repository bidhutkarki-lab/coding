import java.util.*;

/*
 * Leetcode 71 Simplify Path Variant
 * In this version, you're given:

 * cwd: a string representing the current working directory (e.g., /usr/bin)

 * cd: a string that mimics a Unix cd command input (e.g., ../lib or /etc/)

 * 👉 You need to apply the destination path on top of currentDir, then simplify it into its canonical absolute path form.

 */
public class part1 {

    private static String cd(String currentDir, String destination) {

        // destination is relative: apply it on top of currentDir
        String path = currentDir + "/" + destination;

        // simplify path (OG)
        Stack<String> stack = new Stack<>();

        for(String dir : path.split("/")) {
            if(dir.equals("") || dir.equals(".")) {
                continue;
            } else if(dir.equals("..")) {
                if(!stack.isEmpty()) {
                    stack.pop();
                }
            } else {
                stack.push(dir);
            }
        }

        return "/" + String.join("/", stack);
    }

    public static void main(String[] args) {
        System.out.println(cd("/usr/bin", "../lib")); // /usr/lib
        System.out.println(cd("/home/user", "./docs")); // /home/user/docs
        System.out.println(cd("/a/b/c", "../../x/./y//")); // /a/x/y
        System.out.println(cd("/", "..")); // /
    }
}
