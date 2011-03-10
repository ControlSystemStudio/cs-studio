/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.desy.collection.immutable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Immutable hashmap.
 *
 * @author bknerr (bastian.knerr@desy.de)
 *
 * @param <K> type of key
 * @param <V> type of value
 * @see java.util.HashMap
 */
public class ImmutableHashMap<K, V> implements IImmutableMap<K, V> {

    private static final long serialVersionUID = 6946995508361991289L;

    /**
     * Internal helper class implementing the native java.util.Map.Entry.
     * @author bknerr
     *
     * @param <K>
     * @param <V>
     */
    public static final class Entry<K, V> implements Map.Entry<K, V> {
        private final K _key;
        private V _value;
        public Entry(@Nonnull final K key, @Nullable final V value) {
            _key = key;
            _value = value;
        }
        @Override
        @Nonnull
        public K getKey() {
            return _key;
        }
        @Override
        @CheckForNull
        public V getValue() {
            return _value;
        }
        @Override
        @CheckForNull
        public V setValue(@Nullable final V value) {
            _value = value;
            return _value;
        }
    }

    private final Map<K, V> _delegate;

    public ImmutableHashMap() {
        _delegate = Maps.newHashMap();

    }

    public ImmutableHashMap(final int initialCapacity) {
        _delegate = new MapMaker().initialCapacity(initialCapacity).makeMap();
    }

    public ImmutableHashMap(@Nonnull final Map<? extends K, ? extends V> m) {
        _delegate = Maps.newHashMap(m);
    }

    public ImmutableHashMap(@Nonnull final IImmutableMap<? extends K, ? extends V> m) {
        this();
        for (final java.util.Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            _delegate.put(entry.getKey(), entry.getValue());
        }
    }

    private ImmutableHashMap(@Nonnull final Map<K, V> m, @Nonnull final Entry<K, V>... entries) {
        this(m);
        for (final Entry<K, V> entry : entries) {
            _delegate.put(entry.getKey(), entry.getValue());
        }
    }


    @Override
    @Nonnull
    public Set<K> keySet() {
        return Sets.newHashSet(_delegate.keySet());
    }

    @Override
    @Nonnull
    public Collection<V> values() {
        return Lists.newArrayList(_delegate.values());
    }


    @Override
    @Nonnull
    public Set<Map.Entry<K, V>> entrySet() {

        final Set<Map.Entry<K, V>> copy = Sets.newHashSetWithExpectedSize(size());
        for (final java.util.Map.Entry<K, V> entry : _delegate.entrySet()) {
            copy.add(new Entry<K, V>(entry.getKey(), entry.getValue()));
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return _delegate.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return _delegate.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(@Nonnull final K key) {
        return _delegate.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(@Nonnull final V value) {
        return _delegate.containsValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public V get(@Nonnull final K key) {
        return _delegate.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public IImmutableMap<K, V> with(@Nonnull final K key, @Nonnull final V value) {
        return new ImmutableHashMap<K, V>(_delegate, new Entry<K, V>(key, value));

    }
}
