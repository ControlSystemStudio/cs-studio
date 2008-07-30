package org.csstudio.nams.common.activatorUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OSGiServiceOffers implements Map<Class<?>, Object> {

	private final Map<Class<?>, Object> contents = new HashMap<Class<?>, Object>();

	public void clear() {
		this.contents.clear();
	}

	public boolean containsKey(final Object key) {
		return this.contents.containsKey(key);
	}

	public boolean containsValue(final Object value) {
		return this.contents.containsValue(value);
	}

	public Set<java.util.Map.Entry<Class<?>, Object>> entrySet() {
		return this.contents.entrySet();
	}

	public Object get(final Object key) {
		return this.contents.get(key);
	}

	public boolean isEmpty() {
		return this.contents.isEmpty();
	}

	public Set<Class<?>> keySet() {
		return this.contents.keySet();
	}

	public Object put(final Class<?> key, final Object value) {
		return this.contents.put(key, value);
	}

	public void putAll(final Map<? extends Class<?>, ? extends Object> map) {
		this.contents.putAll(map);
	}

	public Object remove(final Object key) {
		return this.contents.remove(key);
	}

	public int size() {
		return this.contents.size();
	}

	public Collection<Object> values() {
		return this.contents.values();
	}

}
