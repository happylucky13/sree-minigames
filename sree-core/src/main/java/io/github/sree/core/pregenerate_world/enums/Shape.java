package io.github.sree.core.pregenerate_world.enums;

public enum Shape {
    SQUARE("square"),
    CIRCLE("circle"),
    TRIANGLE("triangle"),
    DIAMOND("diamond"),
    PENTAGON("pentagon"),
    HEXAGON("hexagon"),
    STAR("star");

    private final String shapeName;

    Shape(String shapeName) {
        this.shapeName = shapeName;
    }

    public String getName() {
        return shapeName;
    }
}
