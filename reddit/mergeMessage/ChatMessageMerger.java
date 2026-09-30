/**
 * Overlapping message can appear at the begining of the first window.
 * Sort by chatId, now messages from those chat can be added in the result
 * Avoid the overlapping message between two chat
 *
 * Time: O(KlogK + M), K is Ids, M is total message fetched
 * Space: O(K + W), K is sortedIds, W is result
 */
public class ChatMessageMerger {

    private Chat chat;
    private int windowSize;

    private Message message;

    ChatMessageMerger(Chat chat, int windowSize) {
        this.chat = chat;
        this.windowSize = windowSize;
    }

    List<Message> mergeMessage(List<Integer> ids) {

        List<Integer> sortedIds = new ArrayList<>(ids);
        sortedIds.sort(Integer::compare);

        List<Message> result = new ArrayList<>();
        Integer previousChatId = null;
        Integer lastMessageId = null;

        for(int id : sortedIds) {

            // Duplicate id returns the same window
            if(previousChatId != null && id == previousChatId) {
                continue;
            }

            previousChatId = id;

            List<Message> window = chat.getChatmessages(id, windowSize);

            for(Message message : window) {
                if(lastMessageId == null || message.id > lastMessageId) {
                    result.add(message);
                    lastMessageId = message.id;
                }

            }
        }

        result;
    }

    List<Message> mergeMessageWithOptimization(List<Integer> ids) {

        List<Integer> sortedIds = new ArrayList<>(ids);
        sortedIds.sort(Integer::compare);

        List<Message> result = new ArrayList<>();
        Integer previousChatId = null;
        Integer lastMessageId = null;

        for(int id : sortedIds) {

            // Optimization 1: Duplicate id returns the same window
            if(previousChatId != null && id == previousChatId) {
                continue;
            }

            previousChatId = id;

            List<Message> window = chat.getChatmessages(id, windowSize);

            int messagesAfterChatId = 0;

            for(Message message : window) {
                if(message.id > chatId) {
                    messagesAfterChatId++;
                }
                if(lastMessageId == null || message.id > lastMessageId) {
                    result.add(message);
                    lastMessageId = message.id;
                }

            }

            // Optimization2:
            // we have already retrived all the messages, any new chat wouldn't give a new message
            // e.g.
            // messages:       [1, 2, 3, 4, 5, 6]
            // chatIds:    [3, 3, 5, 6]
            // windowSize: 2
            // after 5 retrives message 6, we don't even need to get for chat 6
            if(messagesAfterChatId < windowSize) {
                break;
            }
        }

        result;
    }
}
