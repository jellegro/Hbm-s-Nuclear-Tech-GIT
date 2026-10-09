package com.usanaem.occultic_ntm.anomaly;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AnomalyRegistry {
    private final Map<String, Anomaly> entries = new LinkedHashMap<String, Anomaly>();
    public void register(Anomaly anomaly) {
        if (anomaly == null || !anomaly.id().matches("[a-z0-9_]+") || entries.containsKey(anomaly.id())
                || anomaly.weight() < 1 || anomaly.minimumAttention() < 0)
            throw new IllegalArgumentException("Invalid or duplicate anomaly");
        entries.put(anomaly.id(), anomaly);
    }
    public Anomaly get(String id) { return entries.get(id); }
    public Collection<Anomaly> entries() { return Collections.unmodifiableCollection(entries.values()); }
}
