/**
 * The key is to find the longest palindrome strarting at index 0.
 * Once we find a palindrome, reverse the remaining suffix and add it to the front.
 * Checking every prefix take O(n^2), so use KMP's prefix table. See ShortestPalindromeII
 */
public class ShortestPalindrome {


    public String ShortestPalindrome(String s) {
        int prefixLength = s.length();

        while(prefixLength > 0) {
            if(isPalindrome(s, 0, prefixLength-1)) {
                break;
            }
            prefixLength--;
        }
        // remaning suffix, add it to the front
        String suffix = s.substring(prefixLength);
        String toAdd = new StringBuilder(suffix).reverse().toString();

        return toAdd + s;
    }

    private boolean isPalindrome(String s, int left, int right) {
        while(left < right) {
            if(s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
