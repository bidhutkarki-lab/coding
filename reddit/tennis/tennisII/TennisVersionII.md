# Tennis Game Scoring

Implement a scoring system for one tennis game between two players.

The problem has two phases:

1. A `Game` class that manages numeric scoring.
2. A `Match` class that displays readable tennis scores.

Despite its name, `Match` is only a display wrapper. Sets and full matches are outside the scope.

## Phase 1: Numeric Scoring

Implement this Java API:

    public record Score(int player1Points, int player2Points) {}

    public class Game {
        public Game(String player1, String player2);
        public void addScore(String player);
        public Score getScore();
        public Optional<String> getResult();
    }

### Requirements

- Both players start with 0 points.
- Player names must be distinct and nonblank.
- `addScore(player)` adds one point to that player.
- Reject an unknown player.
- Reject additional points after the game is complete.
- `getScore()` returns the numeric score in player order.
- `getResult()` returns the winner’s name, or an empty `Optional`.

A player wins when both conditions are true:

- Their score is at least 4.
- They lead by at least 2.

| Numeric score | Result |
|---|---|
| 2–0 | Continue |
| 4–0 | Player 1 wins |
| 4–2 | Player 1 wins |
| 4–3 | Continue |
| 5–3 | Player 1 wins |

### Deuce Normalization

After adding a point, if both scores are equal and at least 4, reset both to 3.

    3–3 → Player 1 scores → 4–3
    4–3 → Player 2 scores → 4–4 → reset to 3–3

These numbers represent normalized scoring counts, not the total points actually won.

### Example

Players are `"alice"` and `"bob"`.

| Point events | Numeric score | Winner |
|---|---|---|
| Alice, Bob, Alice, Alice | (3, 1) | None |
| Bob, Bob | (3, 3) | None |
| Alice | (4, 3) | None |
| Bob | (3, 3), after normalization | None |
| Bob | (3, 4) | None |
| Bob | (3, 5) | Bob |

## Phase 2: Readable Score

Implement a `Match` class that owns a `Game`.

    public class Match {
        public Match(String player1, String player2);
        public void pointWonBy(String player);
        public String score();
        public Optional<String> result();
    }

### Requirements

- `pointWonBy(player)` delegates to `Game.addScore(player)`.
- `score()` converts the numeric score into tennis terms.
- `result()` returns `"winner <name>"` when complete, or an empty `Optional`.

### Display Rules

Apply these checks in order:

1. If the game is complete, return `"winner <name>"`.
2. If both counts are at least 3 and equal, return `"deuce"`.
3. If both counts are at least 3 and unequal, return `"advantage <leader>"`.
4. Otherwise, translate each count using the mapping below.

| Numeric count | Display |
|---|---|
| 0 | love |
| 1 | 15 |
| 2 | 30 |
| 3 | 40 |

Join the labels with a hyphen, keeping player 1 first.

### Examples

| Numeric score | Display |
|---|---|
| (0, 0) | love-love |
| (1, 0) | 15-love |
| (2, 2) | 30-30 |
| (3, 2) | 40-30 |
| (3, 3) | deuce |
| (4, 3) | advantage alice |
| (3, 4) | advantage bob |
| (3, 5) | winner bob |

## Alternative: Keep Actual Point Counts

Use this alternative only if deuce normalization is not required.

Instead of resetting tied scores:

- 4–4 stays 4–4.
- 5–5 stays 5–5.

The winning and display rules remain the same:

- At least 4 points with a lead of at least 2 wins.
- Equal counts of at least 3 mean deuce.
- A one-point lead when both have at least 3 means advantage.

| Approach | After reaching 4–4 |
|---|---|
| Normalized version | Returns (3, 3) |
| Actual-count alternative | Returns (4, 4) |

Confirm which behavior is expected before implementing.

## Tests to Include

- Initial score is `"love-love"`.
- A score of 2–0 does not end the game.
- A player wins without reaching deuce.
- Deuce → advantage → deuce.
- Deuce → advantage → winner.
- Repeated advantage exchanges work correctly.
- Unknown players are rejected.
- Points after completion are rejected.

## Complexity

- Record a point: O(1) time.
- Read the score or winner: O(1) time.
- Scoring state: O(1) space.
