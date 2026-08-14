package io.github.sree.molecore.animations;

import io.github.sree.molecore.enums.Objective;
import io.github.sree.molecore.enums.Winner;

public record AnimationRegistry(Winner winner, Objective objective) {
}
