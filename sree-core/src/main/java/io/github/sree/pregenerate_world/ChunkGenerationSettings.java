package io.github.sree.pregenerate_world;

import io.github.sree.pregenerate_world.enums.Pattern;
import io.github.sree.pregenerate_world.enums.Shape;

public record ChunkGenerationSettings(Shape shape, double centerX, double centerZ, double radiusX, double radiusZ, Pattern pattern) {
}
