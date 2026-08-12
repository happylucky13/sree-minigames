package io.github.sree.information;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface InformationChangeListener {
    void onInformationChanged(InformationChangedEvent event);
}