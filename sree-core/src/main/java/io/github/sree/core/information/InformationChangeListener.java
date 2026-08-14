package io.github.sree.core.information;

@FunctionalInterface
public interface InformationChangeListener {
    void onInformationChanged(InformationChangedEvent event);
}