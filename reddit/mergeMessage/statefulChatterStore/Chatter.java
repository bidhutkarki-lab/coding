import java.math.BigDecimal;
import java.util.*;

/**
 * Core idea:
 * - ArrayList keeps messages in ID order because they arrive sorted.
 * - HashMap maps each message ID to its list position, so finding a
 *   message and its two neighbors on each side takes O(1) average time.
 * - For multiple IDs, reuse get_messages, deduplicate by message ID
 *   using a HashMap, then sort the unique messages once.
 * - Edit replaces the message at its existing position. The index stays
 *   valid, and every new read gets the updated content from the list.
 * - No separate result cache is needed because each lookup reads at
 *   most five messages.
 */
public class Chatter {
    public record Message(BigDecimal messageId, String message) {

    }

    private final List<Message> messages = new ArrayList<>();
    private final Map<BigDecimal, Integer> index = new HashMap<>();

    // BigDecimal.equals compares scale, so 123.40 and 123.4 must be normalized to one key.
    private static BigDecimal key(BigDecimal id) {
        return id.stripTrailingZeros();
    }

    public void load(List<Message> batch) {
        for(Message msg : batch) {
            index.put(key(msg.messageId()), messages.size());
            messages.add(msg);
        }
    }

    public List<Message> save() {
        return new ArrayList<>(messages);
    }

    public List<Message> getMessages(BigDecimal chatId) {
        Integer position = index.get(key(chatId));

        if(position == null) {
            return Collections.emptyList();
        }

        int start = Math.max(0, position-2);
        int end = Math.min(messages.size(), position+3);

        return new ArrayList<>(messages.subList(start, end));
    }

    // Complexity: O(K + ulogu) - K = requested Ids, u = unique returned messages
    public List<Message> getMulti(List<BigDecimal> ids) {
        List<BigDecimal> sortedIds = new ArrayList<>(new HashSet<>(ids));
        sortedIds.sort(Comparator.naturalOrder());

        List<Message> result = new ArrayList<>();
        BigDecimal lastIdSeen = null;

        for (BigDecimal id : sortedIds) {
            List<Message> window = getMessages(id);
            if (window.isEmpty()) {
                continue;
            }

            int messagesAfterId = 0;
            for (Message message : window) {
                BigDecimal msgId = message.messageId();
                if (msgId.compareTo(id) > 0) {
                    messagesAfterId++;
                }
                if (lastIdSeen == null || msgId.compareTo(lastIdSeen) > 0) {
                    result.add(message);
                    lastIdSeen = msgId;
                }
            }

            // Fewer than 2 successors means this window reached the last message.
            if (messagesAfterId < 2) {
                break;
            }
        }
        return result;
    }

    // Same position, same ID, so the index and ordering stay valid. O(1) average.
    public void edit(BigDecimal msgId, String text) {
        Integer position = index.get(key(msgId));

        if (position == null) {
            throw new NoSuchElementException("No message with id " + msgId);
        }

        Message existing = messages.get(position);
        messages.set(position, new Message(existing.messageId(), text));
    }

}
