package com.usanaem.occultic_ntm.compat.thaumcraft.aspect.catalogue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * An immutable-by-convention, deterministic "tag -> amount" bag used by the catalogue.
 * Deliberately independent of Thaumcraft/Minecraft types so the catalogue can be evaluated offline and
 * loaded when Thaumcraft is absent. Conversion to Thaumcraft's AspectList happens in the isolated TC package.
 */
public final class AspectAmounts {

    private final Map<String, Integer> amounts = new LinkedHashMap<String, Integer>();

    public AspectAmounts add(String tag, int amount) {
        if (amount <= 0) {
            return this;
        }
		amounts.compute(tag, (k, old) -> old == null ? amount : old + amount);
        return this;
    }

    public int get(String tag) {
        Integer v = amounts.get(tag);
        return v == null ? 0 : v;
    }

    public boolean isEmpty() {
        return amounts.isEmpty();
    }

    public int size() {
        return amounts.size();
    }

    public int total() {
        int t = 0;
        for (int v : amounts.values()) {
            t += v;
        }
        return t;
    }

    /** Insertion-ordered view (strongest-first ordering is established by the catalogue when finalizing). */
    public Map<String, Integer> asMap() {
        return Collections.unmodifiableMap(amounts);
    }

    /** Canonical, sorted rendering: "metallum:4 potentia:2". */
    public String canonical() {
        if (amounts.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : new TreeMap<String, Integer>(amounts).entrySet()) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(e.getKey()).append(':').append(e.getValue());
        }
        return sb.toString();
    }

    /** Parses "tag:n tag:n" (used by tests and tooling). */
    public static AspectAmounts parse(String text) {
        AspectAmounts a = new AspectAmounts();
        for (String part : text.trim().split("\\s+")) {
            if (part.isEmpty()) {
                continue;
            }
            int i = part.indexOf(':');
            a.add(part.substring(0, i), Integer.parseInt(part.substring(i + 1)));
        }
        return a;
    }

    public List<String> tags() {
        return new ArrayList<String>(amounts.keySet());
    }

    @Override
    public String toString() {
        return canonical();
    }
}
