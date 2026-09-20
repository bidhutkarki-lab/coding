# Word Ladder Practice Questions

## 1. LeetCode 127: Word Ladder

Source: [LeetCode 127 — Word Ladder](https://leetcode.com/problems/word-ladder/)

Given `beginWord`, `endWord`, and a dictionary `wordList`, return the number of words in a shortest transformation sequence from `beginWord` to `endWord`. Return `0` if no valid sequence exists.

### Rules

- Each move changes exactly one character using substitution only.
- Every word after `beginWord`, including `endWord`, must be in `wordList`.
- `beginWord` does not need to be in `wordList`.
- Count words in the sequence, including both endpoints, rather than the number of moves.

### Examples

```text
Input:
beginWord = "hit"
endWord = "cog"
wordList = ["hot", "dot", "dog", "lot", "log", "cog"]

Output: 5
```

One shortest sequence is `hit → hot → dot → dog → cog`.

```text
Input:
beginWord = "hit"
endWord = "cog"
wordList = ["hot", "dot", "dog", "lot", "log"]

Output: 0
```

The target `cog` is missing from the dictionary.

### Constraints

- `1 <= beginWord.length <= 10`
- `endWord.length == beginWord.length`
- `beginWord != endWord`
- `1 <= wordList.length <= 5000`
- All dictionary words have the same length as `beginWord`.
- All words contain only lowercase English letters.
- Dictionary words are unique.

### Java Method

```java
public int ladderLength(String beginWord, String endWord, List<String> wordList)
```

## 2. Word Ladder Differ By Two

This is a custom variation of Word Ladder.

Given `beginWord`, `endWord`, and a dictionary `wordList`, return one shortest transformation sequence from `beginWord` to `endWord`. If several shortest sequences exist, return any one. If no valid sequence exists, return an empty list.

### Rules

- Each move changes exactly one or exactly two character positions using substitutions only.
- Every word strictly between the first and last word must be in `wordList`.
- `beginWord` does not need to be in the dictionary.
- `endWord` may be outside the dictionary, provided it is the final word.
- Each move has equal cost; shortest means the fewest moves.
- If `beginWord` equals `endWord`, return `[beginWord]`.

### Examples

```text
Input:
beginWord = "hit"
endWord = "cog"
wordList = ["hot", "dot", "dog", "lot", "log"]

Output: ["hit", "hot", "cog"]
```

`hit → hot` changes one position. `hot → cog` changes two positions. A direct move from `hit` to `cog` is invalid because they differ in three positions.

```text
Input:
beginWord = "aaaa"
endWord = "bbbb"
wordList = ["aabb"]

Output: ["aaaa", "aabb", "bbbb"]
```

Each move changes two positions.

```text
Input:
beginWord = "hit"
endWord = "cog"
wordList = []

Output: []
```

The endpoints differ in three positions, and no intermediate words are available.

### Constraints

- `0 <= wordList.length <= 5000`
- `1 <= beginWord.length == endWord.length <= 10`
- All dictionary words have the same length as `beginWord`.
- All words contain only lowercase English letters.
- Dictionary words are unique.

### Java Method

```java
public List<String> shortestTransformation(
    String beginWord, String endWord, List<String> wordList)
```

## 3. Validate Word Ladder

Given a sequence of lowercase English words, determine whether it is a valid word-transformation sequence. Return `true` if all rules hold; otherwise return `false`.

### Rules

- The sequence must contain at least one word.
- Every adjacent pair must have the same length and differ in exactly one character position.
- No word may appear more than once anywhere in the sequence.
- A single-word sequence is valid.
- Validate the supplied order; do not rearrange words or search for another sequence.

### Examples

```text
Input: ["hot", "dot", "cog"]
Output: false
```

`dot` and `cog` differ in two positions.

```text
Input: ["hot", "dot", "dot"]
Output: false
```

The last two words are identical, and `dot` is repeated.

```text
Input: ["hot", "dot", "dog"]
Output: true
```

Each adjacent pair differs in exactly one position, and all words are unique.

```text
Input: ["hot", "dot", "dog", "hog", "hot"]
Output: false
```

Every adjacent pair differs in exactly one position, but `hot` appears twice.

### Input Format

- First line: an integer `n`, the number of words.
- Second line: `n` space-separated words.

```text
Input:
3
hot dot dog

Output:
true
```

### Constraints

- `1 <= n <= 100000`
- Each word has length from `1` to `100`.
- Input words have equal length and contain only lowercase English letters.
- The validation method should also reject an empty sequence or unequal adjacent word lengths, as required by the rules above.

### Java Method

```java
public boolean isValidSequence(List<String> words)
```
