public enum Side {
    NEAR, FAR;

    public Side other() {
        return this == NEAR ? FAR : NEAR;
    }
}
