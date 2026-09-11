public class MinimumDeletion {
    public static int minimumDeletions(String s) {

        int open = 0;
        int deletions = 0;

        for(int i=0; i<s.length(); i++) {
            if(s.charAt(i) == "(") {
                open++;
            } else if(open > 0) {
                open--;
            } else {
                deletions++;
            }
        }

        return deletions + open;
    }
}
