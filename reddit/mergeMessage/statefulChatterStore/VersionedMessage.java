import java.util.*;

/**
 * Core idea:
 * - Message is the stable identity: an ID plus an append-only list of
 *   immutable MessageVersion records. Edits never touch existing versions.
 * - Edits earlier than the latest version are rejected, so timestamps in each
 *   list are non-decreasing. That makes asOf a binary search.
 * - Equal timestamps are allowed; asOf finds the last version with
 *   timestamp <= t, so the last appended one wins.
 * - Unknown IDs: edit and latest throw NoSuchElementException. asOf returns
 *   Optional.empty(), since the message did not exist at any time.
 *
 * Complexity (V = versions of one message):
 * - create / edit: amortized O(1) (HashMap lookup + ArrayList append)
 * - latest: O(1) (tail of the list)
 * - asOf: O(log V)
 * - storage: O(V) version records per message, excluding content size
 */
public class VersionedMessage {

    public record MessageVersion(int version, String content, long timestamp) {
    }

    private static final class Message {
        final int id;
        final List<MessageVersion> versions = new ArrayList<>();

        Message(int id) {
            this.id = id;
        }

        MessageVersion latest() {
            return versions.get(versions.size() - 1);
        }
    }

    private final Map<Integer, Message> messages = new HashMap<>();

    public void create(int messageId, String content, long createdAt) {
        Objects.requireNonNull(content, "content");
        if (messages.containsKey(messageId)) {
            throw new IllegalArgumentException("Message " + messageId + " already exists");
        }

        Message message = new Message(messageId);
        message.versions.add(new MessageVersion(1, content, createdAt));
        messages.put(messageId, message);
    }

    public void edit(int messageId, String newContent, long editedAt) {
        Objects.requireNonNull(newContent, "newContent");
        Message message = find(messageId);
        MessageVersion current = message.latest();

        if (editedAt < current.timestamp()) {
            throw new IllegalArgumentException(
                    "Edit at " + editedAt + " is earlier than latest version at " + current.timestamp());
        }

        message.versions.add(new MessageVersion(current.version() + 1, newContent, editedAt));
    }

    public MessageVersion latest(int messageId) {
        return find(messageId).latest();
    }

    public Optional<MessageVersion> asOf(int messageId, long timestamp) {
        Message message = messages.get(messageId);
        if (message == null) {
            return Optional.empty();
        }

        // Find the first version with timestamp > t; the one before it is the answer.
        List<MessageVersion> versions = message.versions;
        int lo = 0;
        int hi = versions.size();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (versions.get(mid).timestamp() <= timestamp) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }

        return lo == 0 ? Optional.empty() : Optional.of(versions.get(lo - 1));
    }

    private Message find(int messageId) {
        Message message = messages.get(messageId);
        if (message == null) {
            throw new NoSuchElementException("No message with id " + messageId);
        }
        return message;
    }
}
