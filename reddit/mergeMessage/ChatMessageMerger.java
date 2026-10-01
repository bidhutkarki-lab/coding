/**
 * Overlapping message can appear at the begining of the first window.
 * Sort by chatId, now messages from those chat can be added in the result
 * Avoid the overlapping message between two chat
 *
 * Time: O(KlogK + M), K is Ids, M is total message fetched
 * Space: O(K + W), K is sortedIds, W is result
 */
import java.util.*;

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
        Integer lastMessageId = null;

        for(int id : sortedIds) {

            List<Message> window = chat.getChatmessages(id, windowSize);

            for(Message message : window) {
                if(lastMessageId == null || message.id > lastMessageId) {
                    result.add(message);
                    lastMessageId = message.id;
                }

            }
        }

        return result;
    }

    List<Message> mergeMessageWithOptimization(List<Integer> ids) {

        // Optimization 1: Duplicate id returns the same window, so fetch each unique id once
        List<Integer> sortedIds = new ArrayList<>(new HashSet<>(ids));
        sortedIds.sort(Integer::compare);

        List<Message> result = new ArrayList<>();
        Integer lastMessageId = null;

        for(int id : sortedIds) {

            List<Message> window = chat.getChatmessages(id, windowSize);

            int messagesAfterChatId = 0;

            for(Message message : window) {
                if(message.id > id) {
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
            // messages:   [1, 2, 3, 4, 5, 6]
            // chatIds:    [3, 3, 5, 6]
            // windowSize: 2
            // after 5 retrives message 6, we don't even need to get for chat 6
            if(messagesAfterChatId < windowSize) {
                break;
            }
        }

        return result;
    }
}
