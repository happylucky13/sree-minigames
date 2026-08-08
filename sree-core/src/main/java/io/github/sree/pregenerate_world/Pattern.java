package io.github.sree.pregenerate_world;

public enum Pattern {
    REGION("region"),
    CONCENTRIC("concentric"),
    LOOP("loop"),
    SPIRAL("spiral");

    private final String patternName;

    Pattern(String patternName) {
        this.patternName = patternName;
    }

    public String getName() {
        return patternName;
    }
}