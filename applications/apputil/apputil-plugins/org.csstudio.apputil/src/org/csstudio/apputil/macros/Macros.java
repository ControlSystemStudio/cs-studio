package org.csstudio.apputil.macros;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Macros implements IMacros {

    protected Map<String, String> macrosMap;

    public Macros() {
        macrosMap = new ConcurrentHashMap<String, String>();
    }

    public Macros(Map<String, String> macros) {
        this.macrosMap = new ConcurrentHashMap<String, String>();
        if (macros != null) {
            putAll(macros);
        }
    }

    @Override
    public String get(String key) {
        return macrosMap.get(key);
    }

    @Override
    public void put(String key, String value) {
        macrosMap.put(key, value);
    }

    @Override
    public void putAll(Map<String, String> macros) {
        this.macrosMap.putAll(macros);
    }

    @Override
    public void putAll(IMacros macros) {
        for (String s : macros.keySet()) {
            this.macrosMap.put(s, macros.get(s));
        }
    }

    @Override
    public Set<String> keySet() {
        return macrosMap.keySet();
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        final String names[] = macrosMap.keySet().toArray(new String[macrosMap.size()]);
        for (String name: names)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(name + "=\"" + get(name) + "\"");
        }
        return buf.toString();
    }

}
