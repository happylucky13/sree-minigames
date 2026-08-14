package io.github.sree.core.spectators;

import org.bukkit.entity.Player;

public interface SpectatorVoiceService {

    void addSpectator(Player player);

    void removeSpectator(Player player);
}
