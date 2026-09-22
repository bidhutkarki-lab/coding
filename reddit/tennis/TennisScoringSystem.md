# Tennis Scoring System

Design and implement a tennis scoring system for two players.

There are **two versions** of this problem:

* **Version A:** Build a complete match, starting with games and adding sets and match tracking.
* **Version B:** Build only one game, separating numeric scoring from readable score formatting.

Treat these as separate alternatives.

## Before You Start

Clarify these points with the interviewer:

* Should score methods return numeric counts, formatted strings, or both?
* Should recording a point after completion raise an error?
* Does every set, including the final set, use a tiebreak at 6–6?
* Is a tiebreak played to 7 points, with a two-point lead required?
* Are serving order, side changes, or score history required?

For the requirements below, assume every set uses a tiebreak at 6–6.

---

# Version A: Complete Tennis Match

## Phase 1: Single Game

Implement a `Game` class that records points for two players.

### Rules

The displayed score follows this sequence:

```text
0 → 15 → 30 → 40 → game
```

A player wins when they have:

* At least 4 points.
* At least 2 more points than their opponent.

When both players have at least 3 points:

* Equal points means **deuce**.
* A one-point lead means **advantage**.
* A two-point lead wins the game.

### Required Operations

```text
recordPoint(player)
getScore()
getWinner()
```

* `recordPoint` records one point for the given player.
* `getScore` returns the displayed game score.
* `getWinner` returns the winner, or no result if the game is ongoing.

### Example

```text
Points       Display
A            15-0
A            30-0
B            30-15
A            40-15
B            40-30
B            deuce
A            advantage A
B            deuce
B            advantage B
B            winner B
```

## Phase 2: Set Scoring

Implement a `TennisSet` class that tracks games.

### Rules

* Each completed game adds one game win to its winner.
* Start a new game if the set is still ongoing.
* A player wins the set by winning at least 6 games with a lead of at least 2 games.
* At **6–6**, start a tiebreak.

Examples:

| Game score | Result           |
| ---------- | ---------------- |
| 6–4        | A wins the set   |
| 6–5        | Continue playing |
| 7–5        | A wins the set   |
| 6–6        | Start a tiebreak |

### Tiebreak Rules

* Points are displayed as ordinary numbers: `0, 1, 2, 3, ...`.
* The first player to reach at least 7 points with a lead of at least 2 wins.
* The tiebreak winner wins the set **7–6**.

Examples:

| Tiebreak points | Result           |
| --------------- | ---------------- |
| 7–5             | A wins           |
| 7–6             | Continue playing |
| 8–6             | A wins           |
| 10–8            | A wins           |

### Required State

Expose:

* Games won by each player.
* Current game or tiebreak score.
* Whether a tiebreak is active.
* Set winner, if decided.

## Phase 3: Best-of-N Match

Implement a `Match` class that tracks sets.

### Rules

Support both formats through a constructor parameter:

| Format    | Sets needed to win |
| --------- | ------------------ |
| Best of 3 | 2                  |
| Best of 5 | 3                  |

* Each completed set adds one set win to its winner.
* Start another set if the match is still ongoing.
* End the match when a player reaches the required number of set wins.
* Reject additional point events after the match is complete.

### Required Operations

```text
recordPoint(player)
getState()
getWinner()
isComplete()
```

### Required Match State

Expose:

* Sets won by each player.
* Scores of completed sets.
* Games won in the current set.
* Current game or tiebreak score.
* Match winner, if decided.

Example:

```text
Format: Best of 3
Sets won: A 1, B 0
Completed sets: 6-4
Current set: 3-2
Current game: 30-15
Match winner: none
```

## Phase 4: Test Harness

Create a test driver that accepts a sequence of point winners.

Example:

```text
runTests("AABBABAA")
```

For each character:

1. Record a point for that player.
2. Print the updated score.

Also write tests that assert the expected state.

### Required Test Cases

* A player wins a game without reaching deuce.
* Deuce → advantage → deuce.
* Deuce → advantage → game.
* A set ends at 6–4.
* A set continues at 6–5 and ends at 7–5.
* A tiebreak begins at 6–6.
* A tiebreak continues at 7–6 and ends at 8–6.
* A best-of-three match ends after 2 set wins.
* A best-of-five match ends after 3 set wins.
* A point recorded after match completion is rejected.

## Phase 5: Optional Extensions

The interviewer may request one or more extensions.

### Option A: Serving Rotation

Track the current server.

Clarify the serving rules for regular games, tiebreaks, and the game after a tiebreak.

### Option B: Score History

Support queries such as:

```text
getStateAfterPoint(17)
```

This should return the state immediately after the 17th point.

Define whether point `0` represents the initial state.

### Option C: Side Changes

Track which side each player occupies.

For the basic extension, players switch sides after odd-numbered games: `1, 3, 5, ...`.

Clarify side changes during tiebreaks and across set boundaries. Keep this metadata separate from point scoring.

### Option D: Match Format Abstraction

Support best-of-three and best-of-five without duplicating game or set logic.

A constructor parameter is sufficient unless the interviewer specifically requests separate match types.

## Suggested Class Responsibilities

| Class          | Responsibility                                           |
| -------------- | -------------------------------------------------------- |
| `Game`         | Regular point scoring, deuce, advantage, and game winner |
| `TiebreakGame` | Numeric points and first-to-7, win-by-2 scoring          |
| `TennisSet`    | Game wins, tiebreak transition, and set winner           |
| `Match`        | Set wins, match format, and match winner                 |

Regular games and tiebreaks can share a scoring interface.

Store numeric points and derive displayed scores when requested. Avoid storing numeric and displayed scores as separate mutable state.

---

# Version B: Single Game with a Separate Renderer

This alternative covers **one game only**. It does not include sets or a full tennis match.

The class named `Match` below is a formatting wrapper around `Game`.

## Phase 1: Numeric Game API

Implement:

```python
class Game:
    def __init__(self, player1: str, player2: str): ...

    def add_score(self, player: str) -> None: ...

    def get_score(self) -> tuple[int, int]: ...

    def get_result(self) -> str | None: ...
```

### Requirements

* Initialize both players with 0 points.
* `add_score` adds one point for the given player.
* Reject points after the game has a winner.
* `get_score` returns the numeric scoring state.
* `get_result` returns the winner’s name, or `None`.

A player wins when:

```text
Their points >= 4
AND
Their lead >= 2
```

### Deuce Normalization

For this version, normalize a tied score of `4–4` back to `3–3`.

```text
3-3 → A scores → 4-3
4-3 → B scores → 4-4 → normalize to 3-3
```

Apply this whenever both counts are equal and at least 4.

Because of normalization, these numbers represent the **current scoring state**, not the total points actually won.

### Example

```text
Events: A, B, A, A
Score: (3, 1)
Result: None

Events: B, B
Score: (3, 3)
Result: None

Event: A
Score: (4, 3)
Result: None

Event: B
Score: (3, 3)
Result: None
```

## Phase 2: Readable Score Renderer

Implement a wrapper:

```python
class Match:
    def __init__(self, player1: str, player2: str): ...

    def point_won_by(self, player: str) -> None: ...

    def score(self) -> str: ...

    def result(self) -> str | None: ...
```

### Requirements

* `point_won_by` delegates to `Game.add_score`.
* `score` converts the numeric state into readable tennis terms.
* `result` returns `"winner <player>"`, or `None`.

### Rendering Rules

Check for a winner before checking advantage.

| Numeric state           | Display         |
| ----------------------- | --------------- |
| `(0, 0)`                | `"love-love"`   |
| `(1, 0)`                | `"15-love"`     |
| `(2, 2)`                | `"30-30"`       |
| `(3, 2)`                | `"40-30"`       |
| `(3, 3)`                | `"deuce"`       |
| `(4, 3)`                | `"advantage A"` |
| `(3, 4)`                | `"advantage B"` |
| Any winning state for A | `"winner A"`    |

### Example

```text
A scores                  → "15-love"
Both players reach 3      → "deuce"
A scores                  → "advantage A"
B scores                  → "deuce"
B scores                  → "advantage B"
B scores                  → "winner B"
```

## Important Correctness Checks

* A lead of 2 is not enough by itself. A score of `2–0` must not end the game.
* Returning to deuce must remove the previous advantage.
* Keep numeric scoring and formatted output in separate methods.
* Deuce normalization is required for Version B as specified. Version A can keep actual point counts and correctly derive deuce and advantage without normalization.
