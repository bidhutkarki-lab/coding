import java.util.*;

public class ModSystemII {

    private final Map<String, Map<String, Long>> communityMods = new HashMap<>();

    public ModSystem(List<String> logs) {
        for(String log : logs) {
            String[] parts = log.split(",");

            String community = parts[0].trim();
            String targetuser = parts[1].trim();
            String action = parts[2].trim();

            // parts[3] is actorUser; not needed for replay
            long timestamp = Long.parseLong(parts[4].trim());

            Map<String, Long> mods = communityMods.computeIfAbsent(community, key -> new HashMap<>());

            switch(action) {
                case "added":
                    mods.put(targetUser, timestamp);
                    break;
                case "remove":
                    mods.remove(targetUser);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown action: " + action);
            }
        }
    }

    public boolean canRemoveMod(
        String community,
        String targetUser,
        String actorUser
    ) {
        if(targetUser.equals(actorUser)) {
            return false;
        }

        Map<String, Long> mods = communityMods.get(community);
        if(mods == null) {
            return false;
        }

        Long targetAddedAt = mods.get(targetUser);
        Long actorAddedAt = mods.get(actorUser);

        if(targetAddedAt == null || actorAddedAt == null) {
            return false;
        }

        return actorAddedAt < targetAddedAt;
    }

    public List<String> getModRanking(String community) {
        Map<String, Long> mods = communityMods.get(community);

        if(mods == null) {
            return new ArrayList<>();
        }

        List<String> ranking = new ArrayList<>(mods.keySet());
        ranking.sort(Comparator.comparingLong(mods::get));

        return ranking;
    }

    public void demote(String community, String user) {
        Map<String, Long> mods = communityMods.get(community);

        if(mods == null || !mods.containsKey(user)) {
            return;
        }

        List<String> ranking = getModRanking(community);
        int index = ranking.indexOf(user);

        // already a lowest-ranked moderator
        if(index == ranking.size()-1) {
            return;
        }

        String nextuser = ranking.get(index+1);

        Long userPriority = mods.get(user);
        Long nextPriority = mods.get(nextUser);

        mods.put(user, nextPriority);
        mods.put(nextUser, userPriority);
    }
}
