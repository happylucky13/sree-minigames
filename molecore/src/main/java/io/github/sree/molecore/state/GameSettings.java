package io.github.sree.molecore.state;

import io.github.sree.molecore.enums.Objective;

public record GameSettings(int moleCount, Objective objective, int gracePeriodSeconds) { }
