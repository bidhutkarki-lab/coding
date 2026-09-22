/**
 * Similar to 29. Merge K Sorted list
 * Use Min heap for sorting by timestamp and then chatIndex
 *
 * Add the first item (Message) of all the chats to heap (which will be sorted)
 * Pop the smallest item from the heap (add that to the result), then add another item from the same chat.
 *
 * Time: O(k + Nlog(K)) - k is the number of chats, N is number of messages
 * Space: O(K) + O(N), K for heap and N for returned result
 */
public class MergeMessage {
    public record Message (
        String messageId,
        long timestamp,
        String sender,
        String text
    ) {}

    public static class Solution {
        private record Entry (
            Message message,
            int chatIndex,
            int messageIndex
        ) {}

        public List<String> mergeMessages(List<List<Message>> chats) {

            PriorityQueue<Entry> heap = new PriorityQueue<>(
                Comparator.comparingLong((Entry e) -> e.message().timestamp())
                .thenComparingInt(Entry::chatIndex)
            );

            // start with the first message for each non-empty chat
            for(int i=0; i<chats.size(); i++) {
                if(!chats.get(i).isEmpty()) {
                    heap.offer(new Entry(chats.get(i).get(0), i, 0));
                }
            }

            List<String> result = new ArrayList<>();

            while(!heap.isEmpty()) {
                // pop the smallest message
                Entry smallest = heap.poll();

                // Add the next candidate form the same list
                int nextIndex = smallest.messageIndex() + 1;
                List<Message> chat = chats.get(smallest.chatIndex());

                if(nextIndex < chat.size()) {
                    heap.offer(new Entry(chat.get(nextIndex), smallest.chatIndex(), nextIndex));
                }

                // add smallest to the result
                result.add(smallest.message().messageId());
            }

            return result;
        }
    }
}
