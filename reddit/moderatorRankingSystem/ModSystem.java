import java.util.*;


public class ModSystem {

    public ModSystem(List<String> logs) {
        for(String log : logs) {
            String[] parts = log.split(",");

            String targetuser = parts[0].trim();
            String action = parts[1].trim();

            // parts[2] is actorUser; not needed for replay
            long timestamp = Long.parseLong(parts[3].trim());

            switch(action) {
                case "added":
                    activeMods.put(targetuser, timestamp);
                    break;
                case "removed":
                    activeMods.remove(targetUser);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown action: " + action);
            }
        }
    }

    public boolean canRemoveMod(String targetUser, String actorUser) {
        if(targetUser.equals(actorUser)) {
            return false;
        }

        Long targetAddedAt = activeMods.get(targetUser);
        Long actorAddedAt = activeMods.get(actorUser);

        if(targetAddedAt == null || actorAddedAt == null) {
            return false;
        }

        return actorAddedAt < targetAddedAt;
    }

    public List<String> getModRanking() {
        List<String> ranking = new ArrayList<>(activeMods.keySet());
        ranking.sort(Comparactor.comparingLong(activeMods::get));

        return ranking;
    }




}
