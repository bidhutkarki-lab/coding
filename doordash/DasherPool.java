import java.util.*;

public class DasherPool {

    private final List<Integer> dashers;
    private final Map<Integer, Integer> index;
    private final Random random = new Random();

    public DasherPool(List<Integer> dasherIds) {
        dashers = new ArrayList<>(dasherIds);
        index = new HashMap<>();

        for (int i = 0; i < dashers.size(); i++) {
            index.put(dashers.get(i), i);
        }
    }

    public int pickDasher() {
        if (dashers.isEmpty()) {
            throw new NoSuchElementException("No available dashers");
        }

        int selectedIndex = random.nextInt(dashers.size());
        int selectedId = dashers.get(selectedIndex);

        int lastIndex = dashers.size() - 1;
        int lastId = dashers.get(lastIndex);

        dashers.set(selectedIndex, lastId);
        // here is the bug, if the selectedIndex is lastId then we are removing and adding it back immediately
        // we should put first then remove
        index.remove(selectedId);
        index.put(lastId, selectedIndex);
        dashers.remove(lastIndex);

        return selectedId;
    }

    // FOR TEST
    // Inject a mock/stub Random to force first, middle, and last index selections.
}
