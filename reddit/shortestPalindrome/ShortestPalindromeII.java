/**
 * We need to have Longest proper prefix that is also suffix
 * Basically the longest common prefix and suffix in "string + # + reversed.
 * The last value of prefix table gives the longest prefix matching a suffix of this combined string
 *
        index: 0 1 2 3 4 5 6 7 8
        char:  a b a b # b a b a
        LPS:   0 0 1 2 0 0 1 2 3


        char: a a c e c a a a # a a a c e c a a
        LPS:  0 1 0 0 0 1 2 2 0 1 2 2 3 4 5 6 7

 *
 */
public class ShortestPalindromeII {

    public String shortestPalindrome(String s) {

        String reversed = new StringBuilder(s).reverse().toString();

        String combined = s + "#" + reversed;

        int[] lps = new int[combined.length()];
        int i = 1;

        int prefixLength = 0;

        while(i < combined.length()) {
            if(combined.charAt(i) == combined.charAt(prefixLength)) {
                prefixLength++;
                lps[i] = prefixLength;
                i++;
            } else if(prefixLength > 0) {
                // Try a shorter prefix with the SAME current character
                prefixLength = lps[prefixLength - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        int palindromeLength = lps[combined.length() -1];
        String toAdd = reversed.substring(0, s.length() - palindromeLength);

        return toAdd + s;


    }
}
