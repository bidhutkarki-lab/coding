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
    public record Message(double chat_msg_id, String message) {

    }

    private final List<Message> messages = new ArrayList<>();
    private final Map<Double, Integer> index = new HashMap<>();

    public void load(List<Message> batch) {
        for(Message msg : batch) {
            index.put(msg.chat_msg_id(), messages.size());
            messages.add(msg);
        }
    }

    public List<Message> save() {
        return new ArrayList<>(messages);
    }

    public List<Message> get_messages(double msgId) {
        Integer position = index.get(msgId);

        if(position == null) {
            return Collections.emptyList();
        }

        int start = Math.max(0, position-2);
        int end = Math.min(messages.size(), position+3);

        return new ArrayList<>(messages.subList(start, end));
    }

    // Complexity: O(K + ulogu) - K = requested Ids, u = unique returned messages
    public List<Message> get_multi(List<Double> ids) {
        Map<Double, Message> unique = new HashMap<>();

        for(double id : ids) {
            for(Message msg : get_messages(id)) {
                unique.put(msg.chat_msg_id(), msg);
            }
        }

        List<Message> result = new ArrayList<>(unique.values());
        result.sort(Comparator.comparingDouble(Message::chat_msg_id));
        return result;
    }

    public void edit(double msgId, String text) {
        Integer position = index.get(msgId);

        if(position == null) {
            return;
        }
        // this is why we have arraylist instead of hashset with hashamp
        // in hashset you can get things by the value only, in list you can get by value and index
        Message existing = messages.get(position);
        messages.set(position, new Message(existing.chat_msg_id(), text));
    }

}
