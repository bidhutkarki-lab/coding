public enum MatchFormat {
    BEST_OF_3(3), BEST_OF_5(5);

    public int bestOf;

    private MatchFormat(int bestOf)  {
        this.bestOf = bestOf;
    }

    public int setsNeeded() {
        return (bestOf / 2) + 1;
    }
}
