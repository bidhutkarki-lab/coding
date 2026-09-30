# Moderator Ranking System

Build a system that tracks active moderators, their ranking, and whether one moderator can remove another.

## Part 1: Initialize the System

Implement:

```java
class ModSystem {
    public ModSystem(List<String> logs);
}
```

Each log entry has the format:

```text
<targetUser>,<action>,<actorUser>,<timestamp>
```

| Field | Meaning |
|---|---|
| `targetUser` | User whose moderator status changes |
| `action` | `added` or `removed` |
| `actorUser` | User who performed the action |
| `timestamp` | Integer timestamp represented as a string |

Example:

```text
alice,added,system,100
bob,added,alice,200
charlie,added,alice,300
bob,removed,alice,400
bob,added,alice,500
```

### Requirements

- Logs are provided in increasing timestamp order.
- An `added` event makes the target user an active moderator.
- A `removed` event makes the target user inactive.
- Among active moderators, an earlier **most recent add timestamp** means a higher rank.
- Removing and later adding a moderator resets that user's seniority.

After replaying the example logs, the ranking is:

```text
alice → charlie → bob
```

Bob ranks below Charlie because Bob's most recent add timestamp is `500`.

## Part 2: Check Removal Permission

Implement:

```java
public boolean canRemoveMod(String targetUser, String actorUser);
```

Return `true` only when all three conditions hold:

1. Both users are currently moderators.
2. The actor and target are different users.
3. The actor ranks higher than the target.

Otherwise, return `false`.

Using the example above:

```java
canRemoveMod("bob", "alice");     // true
canRemoveMod("bob", "charlie");   // true
canRemoveMod("alice", "bob");     // false
canRemoveMod("alice", "alice");   // false
canRemoveMod("david", "alice");   // false
```

This method only checks permission; it does not remove the target.

## Part 3: Return the Moderator Ranking

Implement:

```java
public List<String> getModRanking();
```

Return all active moderators from highest rank to lowest rank.

For the example above:

```java
["alice", "charlie", "bob"]
```

Return an empty list when there are no active moderators.

## Follow-up 1: Support Multiple Communities

Extend the system so that moderator membership and ranking are independent within each community.

For this extension, use the log format:

```text
<community>,<targetUser>,<action>,<actorUser>,<timestamp>
```

Update the query methods:

```java
public boolean canRemoveMod(
    String community,
    String targetUser,
    String actorUser
);

public List<String> getModRanking(String community);
```

### Requirements

- A user may moderate multiple communities.
- A user's seniority in one community does not affect another.
- Permission checks compare users within the specified community.
- For an unknown community, return `false` for permission checks and an empty ranking.

Example:

```text
java,alice,added,system,100
java,bob,added,alice,200
python,bob,added,system,300
python,alice,added,bob,400
```

Expected rankings:

```text
java:   alice → bob
python: bob → alice
```

## Follow-up 2: Demote a Moderator

Implement:

```java
public void demote(String community, String user);
```

Demoting a moderator moves that user down **exactly one position** in the current community ranking.

### Requirements

- Swap the user with the moderator immediately below them.
- If the user is already last, do nothing.
- If the user is not an active moderator in that community, do nothing.
- If the community does not exist, do nothing.
- Demotion does not remove moderator status.
- Future ranking and permission queries must reflect the updated order.
- Other communities remain unaffected.

Example:

```text
Initial:
alice → bob → charlie → david

demote("java", "bob"):
alice → charlie → bob → david

demote("java", "bob"):
alice → charlie → david → bob

demote("java", "bob"):
alice → charlie → david → bob
```

Once demotion is supported, **the current ranking determines removal permission**. Add timestamps alone no longer represent the full order.

## Follow-up 3: Scale to Millions of Log Entries

Discuss how you would support a large log history and frequent queries.

Cover:

1. **Log processing**
   - Can entries be replayed in one pass?
   - How would you process input from a stream instead of a preloaded list?
   - What are the allocation trade-offs between `split(",")` and manual parsing?

2. **Memory**
   - What state must remain after replay?
   - Must removed users remain in memory?
   - Can memory depend on current moderators instead of total log count?

3. **Query performance**
   - How would you achieve `O(1)` permission checks after an `O(N)` build?
   - What does returning the full ranking cost?
   - How would demotion affect the data structures and complexity?

4. **Distribution**
   - How would you shard state by community?
   - How would you preserve event order within each community?

## Clarifications Before Coding

Confirm these behaviors with the interviewer:

- Are the action literals `added` / `removed` or `add` / `delete`?
- Are historical logs authoritative, or should replay validate each actor's permission?
- Does adding an already-active moderator reset seniority, do nothing, or raise an error?
- Is removing a user who was never added ignored or rejected?
- Are timestamps guaranteed to be strictly increasing? If ties are allowed, how are they resolved?
- Should malformed or out-of-order entries be ignored or rejected?
- Are log updates supported after initialization? If so, how should they interact with demotion?

## Preparation Notes — Solution Hints

- The base version can use maps for active membership and most recent add timestamps.
- An active-only map can also represent membership through key presence.
- For community scope, keep independent state per community.
- A doubly linked list plus `user → node` lookup supports an adjacent demotion in `O(1)`, but the list alone does not support `O(1)` rank comparisons.
- An array/list plus `user → index` lookup also supports adjacent demotion in `O(1)` by swapping two entries and updating their indices. Without that lookup, locating the user costs `O(M)`.
- Distinguish initialization cost, permission-check cost, ranking-output cost, and demotion cost.

For interview practice, finish Parts 1–3 before optimizing the follow-ups.
