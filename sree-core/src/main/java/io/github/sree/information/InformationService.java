package io.github.sree.information;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InformationService {

    private final Map<UUID, EnumSet<InformationChannel>> information = new HashMap<>();
}
