package io.github.sree.core.pregenerate_world.enums;

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