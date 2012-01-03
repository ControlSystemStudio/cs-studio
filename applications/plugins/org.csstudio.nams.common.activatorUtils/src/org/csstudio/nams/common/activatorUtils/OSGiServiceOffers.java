
package org.csstudio.nams.common.activatorUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OSGiServiceOffers implements Map<Class<?>, Object> {

	private final Map<Class<?>, Object> contents = new HashMap<Class<?>, Object>();

	@Override
    public void clear() {
		this.contents.clear();
	}

    @Override
	public boolean containsKey(final Object key) {
		return this.contents.containsKey(key);
	}

    @Override
	public boolean containsValue(final Object value) {
		return this.contents.containsValue(value);
	}

    @Override
	public Set<java.util.Map.Entry<Class<?>, Object>> entrySet() {
		return this.contents.entrySet();
	}

    @Override
	public Object get(final Object key) {
		return this.contents.get(key);
	}

    @Override
	public boolean isEmpty() {
		return this.contents.isEmpty();
	}

    @Override
	public Set<Class<?>> keySet() {
		return this.contents.keySet();
	}

    @Override
	public Object put(final Class<?> key, final Object value) {
		return this.contents.put(key, value);
	}

    @Override
	public void putAll(final Map<? extends Class<?>, ? extends Object> map) {
		this.contents.putAll(map);
	}

    @Override
	public Object remove(final Object key) {
		return this.contents.remove(key);
	}

    @Override
	public int size() {
		return this.contents.size();
	}

    @Override
	public Collection<Object> values() {
		return this.contents.values();
	}
}
