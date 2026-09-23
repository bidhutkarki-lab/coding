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
