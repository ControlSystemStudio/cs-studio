package org.csstudio.apputil.macros;

import java.util.Map;
import java.util.Set;

public interface IMacros {

    public String get(String key);

    public void put(String key, String value);

    public void putAll(Map<String, String> macros);

    public void putAll(IMacros macros);

    public Set<String> keySet();
}
