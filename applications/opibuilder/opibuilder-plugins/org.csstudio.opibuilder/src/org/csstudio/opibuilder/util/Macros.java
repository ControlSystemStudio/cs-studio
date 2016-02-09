package org.csstudio.opibuilder.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Macros implements IMacros {

    protected Map<String, String> macrosMap;

    public Macros() {
        macrosMap = new HashMap<String, String>();
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

}
