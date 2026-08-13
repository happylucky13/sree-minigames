package io.github.sree.combat_tag;

import java.util.UUID;

public record DamageInstance(UUID attackerId, double damageDealt) {
}
