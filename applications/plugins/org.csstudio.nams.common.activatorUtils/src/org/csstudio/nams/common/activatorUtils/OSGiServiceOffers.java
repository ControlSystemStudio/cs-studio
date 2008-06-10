package org.csstudio.nams.common.activatorUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class OSGiServiceOffers implements Map<Class<?>, Object> {

	private Map<Class<?>, Object> contents;
	
	public void clear() {
		contents.clear();
	}

	public boolean containsKey(Object key) {
		return contents.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return contents.containsValue(value);
	}

	public Set<java.util.Map.Entry<Class<?>, Object>> entrySet() {
		return contents.entrySet();
	}

	public Object get(Object key) {
		return contents.get(key);
	}

	public boolean isEmpty() {
		return contents.isEmpty();
	}

	public Set<Class<?>> keySet() {
		return contents.keySet();
	}

	public Object put(Class<?> key, Object value) {
		return contents.put(key, value);
	}

	public void putAll(Map<? extends Class<?>, ? extends Object> map) {
		contents.putAll(map);
	}

	public Object remove(Object key) {
		return contents.remove(key);
	}

	public int size() {
		return contents.size();
	}

	public Collection<Object> values() {
		return contents.values();
	}

}
