package io.github.sree.pregenerate_world;

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
