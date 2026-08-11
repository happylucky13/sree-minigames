package io.github.sree.state;

import io.github.sree.enums.Objective;

public record GameSettings(int moleCount, Objective objective, int gracePeriodSeconds) { }
