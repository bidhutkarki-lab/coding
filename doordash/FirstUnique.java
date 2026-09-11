public class FirstUnique {

    private final Set<Integer> seen = new HashSet<>();
    private final LinkedHashSet<Integer> unique = new LinkedHashSet<>();

    public void add(int resturantId) {
        if(seen.add(resturantId)) {
            unique.add(restruantId);
        } else {
            unique.remove(resturantId);
        }
    }

    public int showFirstUnique() {
        return unique.isEmpty() ? -1 : unique.iterator().next();
    }
}
