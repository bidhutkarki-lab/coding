# Chat Message Merger

## Part 1: Merge Message Windows

A chat application stores messages with unique, strictly increasing integer IDs. IDs are not necessarily consecutive. Message content is immutable.

You are given a read-only `Message` model and a `Chat` API:

```java
class Message {
    int id;
    String content;
}

interface Chat {
    /**
     * Returns the target message, up to windowSize immediate predecessors,
     * and up to windowSize immediate successors, sorted by ID ascending.
     * Includes all available neighbors up to that limit on each side.
     * Returns an empty list if the target does not exist.
     */
    List<Message> getChatMessages(int id, int windowSize);
}
```

Implement:

```java
class ChatMessageMerger {
    ChatMessageMerger(Chat chat, int windowSize);
    List<Message> mergeMessages(List<Integer> ids);
}
```

For every requested ID, retrieve its context window and combine the windows into one list.

### Requirements

- Return messages sorted by ID ascending.
- Include each message at most once, deduplicating by message ID.
- Support unsorted input IDs and duplicate input IDs.
- Ignore nonexistent IDs.
- Return an empty list for empty input.
- Support overlapping and disjoint windows.
- Accept a nonnegative window size; reject negative sizes.
- Assume the chat does not change during one merge operation.
- Do not modify the caller's input list.

The window size counts messages, not the numerical distance between IDs. Every window is a consecutive slice of the same conversation.

### Example

Assume the chat contains IDs `0` through `15`.

```text
windowSize = 5
ids = [5, 1, 3]

Window around 5: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
Window around 1: [0, 1, 2, 3, 4, 5, 6]
Window around 3: [0, 1, 2, 3, 4, 5, 6, 7, 8]

Result IDs: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
```

Example with gaps in IDs:

```text
Chat IDs: [10, 20, 40, 80, 100]
windowSize = 1
ids = [80, 20]

Window around 80: [40, 80, 100]
Window around 20: [10, 20, 40]

Result IDs: [10, 20, 40, 80, 100]
```

### Performance Discussion

Let:

- `K` be the number of requested IDs.
- `M` be the total number of messages returned across API calls, counting overlaps.
- `R` be the number of unique messages in the result.

Exploit the ordering of the windows rather than collecting and sorting all returned messages. Explain time complexity, auxiliary space, output space, and backend-call count separately.

The original prompt asks for better than `O(M log M)`. Clarify this requirement: with unsorted targets and a fixed window size, ordinary comparison-based sorting of targets does not guarantee that strict improvement. Discuss the costs in terms of both `K` and `M`.

## Follow-up 1: Reduce Backend Calls

The backend API is expensive. Reduce calls while preserving exactly the same result.

Discuss:

- Duplicate target IDs within one request.
- Whether seeing a target in a previously fetched window is enough to skip its call.
- Whether detecting the end of the conversation allows remaining calls to be skipped.
- The worst-case number of necessary calls.
- How backend-call complexity differs from message-processing complexity.

Overlap alone does not mean that a window is fully covered:

```text
windowSize = 2

Window around 3: [1, 2, 3, 4, 5]
Window around 5: [3, 4, 5, 6, 7]
```

Skipping the second window would lose messages `6` and `7`.

## Follow-up 2: Add Caching

Add a cache in front of `getChatMessages` to reduce repeated calls across requests.

Discuss:

- Whether to cache individual messages, complete windows, or both.
- Cache keys for a merger with a fixed chat and window size.
- Cache keys for a shared cache serving multiple chats and window sizes.
- Whether to cache empty results.
- Memory limits and eviction policy.
- Invalidation when new messages are appended.
- How the policy changes if edits or deletions are introduced.

Clarify whether the chat is permanently fixed or append-only. Immutable content does not guarantee immutable windows: a window near the end can gain successors after new messages arrive.

## Follow-up 3: Editable Messages and Version History

Messages can now be edited. Preserve every previous version rather than overwriting content.

Separate the stable message identity from its immutable versions. Each version contains a version number, content, and timestamp.

Support:

```java
void edit(int messageId, String newContent, long editedAt);
MessageVersion latest(int messageId);
Optional<MessageVersion> asOf(int messageId, long timestamp);
```

### Requirements

- Store the initial content as the first version, timestamped at creation.
- Each edit appends a new version without changing existing versions.
- The message ID remains unchanged across edits.
- Reject edit timestamps earlier than the message's most recent version.
- Allow equal timestamps; if several versions share a timestamp, the last appended version wins.
- `latest` returns the most recent version.
- `asOf(t)` returns the last version whose timestamp is at most `t`.
- Return no version if the message did not exist at `t`.
- Define how unknown message IDs are handled.

### Example

```text
Message ID: 3

Version 1: time 100, content "Hello"
Version 2: time 120, content "Hello there"
Version 3: time 150, content "Hello everyone"

latest(3)    -> Version 3
asOf(3, 130) -> Version 2
asOf(3, 100) -> Version 1
asOf(3, 90)  -> no version
```

### Expected Complexity

For a message with `V` versions:

| Operation | Expected complexity |
| --- | --- |
| Append an edit | Amortized `O(1)` |
| Read latest version | `O(1)` |
| Read version at a timestamp | `O(log V)` |
| Version-record storage | `O(V)`, excluding content size |

### Snapshot Windows

Extend the chat API to retrieve windows at a snapshot timestamp. Returned messages retain their stable IDs alongside the selected content.

Clarify:

- Whether neighbors are selected only from messages that existed at the snapshot time.
- What happens when the target did not yet exist.
- How all windows in one merge use the same snapshot.
- Why deduplication still uses message ID rather than version number.
- How edits invalidate cached latest-content windows.
- How backdated or equal-timestamp edits affect historical caches.
- How concurrent edits and snapshot reads should behave.

## Alternate Problem: Stateful Chatter Store

This is a separate contract. You own the message collection instead of calling a provided backend service.

Use decimal-valued IDs with an exact representation such as `BigDecimal`. Treat numerically equal IDs, such as `123.40` and `123.4`, as the same identity.

Conceptual Java interface:

```java
class Chatter {
    void load(List<Message> messages);
    List<Message> save();
    List<Message> getMessages(BigDecimal messageId);
    List<Message> getMulti(List<BigDecimal> ids);
    void edit(BigDecimal messageId, String newContent);
}
```

In this variant, `Message.id` uses the chosen decimal ID type.

### Phase 1: Load and Save

- `load` may be called repeatedly.
- IDs are unique.
- Incoming batches arrive in global ID order: every new ID is greater than every previously loaded ID.
- `save` returns all stored messages in ID order.
- Prevent callers from modifying internal storage through returned lists.

### Phase 2: Retrieve One Neighborhood

- `getMessages` returns the target, up to two predecessors, and up to two successors.
- Stop at either boundary of the collection.
- Return an empty list if the target does not exist.
- Count neighboring messages, not differences between decimal IDs.

### Phase 3: Retrieve Multiple Neighborhoods

- `getMulti` returns the union of the neighborhoods for all requested IDs.
- Sort the result by ID ascending.
- Include each message at most once.
- Support unsorted IDs, duplicates, missing targets, and empty input.

### Example

Load these batches in order:

```text
Batch 1: [123.41, 123.43, 123.45]
Batch 2: [126.56, 126.57, 128.61, 129.62, 130.65, 132.67, 134.68]
```

Expected results:

```text
getMessages(123.41)
-> [123.41, 123.43, 123.45]

getMulti([128.61, 130.65])
-> [126.56, 126.57, 128.61, 129.62, 130.65, 132.67, 134.68]
```

### Phase 4: Edit Content

- Update a message's content without changing its ID or position.
- Define behavior for a nonexistent message ID.
- Revisit any cached results affected by the edit.

### Optional Extensions

1. Cache single-ID and multi-ID queries; discuss whether local lookups justify caching.
2. Define invalidation after loading more messages or editing content.
3. Retain all old versions instead of replacing content.
4. Support latest-version and historical snapshot reads using the version-history requirements above.

### Performance Discussion

Explain construction and storage costs, per-operation complexity, and the trade-offs of your data structures. Separate output-copying costs from lookup costs.
